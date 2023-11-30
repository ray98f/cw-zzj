package com.zzj.config.aspect;

import com.zzj.annotation.PermissionCheck;
import com.zzj.enums.ErrorCode;
import com.zzj.exception.CommonException;
import com.zzj.shiro.CurrentLoginUser;
import com.zzj.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author frp
 */
@Aspect
@Component
@Slf4j
public class PermissionCheckAspect {

    @Value("${spring.redis.key-prefix}")
    private String keyPrefix;

    public static final String USER_ROLE = "user-roles:";
    public static final String STRING_NULL = "";
    public static final String AUTHORIZATION = "Authorization";
    public static final String ARG_NAMES = "pjp,permissionCheck";
    public static final String REGEX;

    static {
        REGEX = ",";
    }


    //@Autowired
    //private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Spring中使用@Pointcut注解来定义方法切入点
     *
     * @Pointcut 用来定义切点，针对方法
     * @Aspect 用来定义切面，针对类 后面的增强均是围绕此切入点来完成的
     * 此处仅配置被我们刚才定义的注解：AuthToken修饰的方法即可
     */
    @Pointcut("@annotation(permissionCheck)")
    public void doPermissionCheck(PermissionCheck permissionCheck) {
    }

    /**
     * 此处我使用环绕增强，在方法执行之前或者执行之后均会执行。
     */
    @Around(value = "doPermissionCheck(permissionCheck)", argNames = ARG_NAMES)
    public Object deBefore(ProceedingJoinPoint pjp, PermissionCheck permissionCheck) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        // 获取访问该方法所需的permissionName信息
        String[] permissionName = permissionCheck.permissionName();
        if (permissionName.length == 0) {
            String userName = TokenUtil.getCurrentUserName();
            if (userName != null && !STRING_NULL.equals(userName)) {
                return pjp.proceed();
            }
            throw new CommonException(ErrorCode.USER_NOT_LOGIN_IN);
        } else {
            String token = request.getHeader(AUTHORIZATION);
            CurrentLoginUser currentLoginUser = TokenUtil.getSimpleTokenInfo(token);
            if ("admin".equals(currentLoginUser.getPersonNo()) && "admin".equals(currentLoginUser.getPersonName())) {
                return null;
            }
            String userRoleKey = keyPrefix + USER_ROLE + currentLoginUser.getPersonNo();
            String lockKey = keyPrefix + USER_ROLE + currentLoginUser.getPersonNo();
            //RLock locker = redissonClient.getLock(lockKey);
            List<String> roles = new ArrayList<>();
            try {
                //locker.lock();
                if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(userRoleKey))) {
                    //roles = roleMapper.selectMenuRoles(currentLoginUser.getPersonNo());
                    stringRedisTemplate.opsForValue().set(userRoleKey, String.join(REGEX, roles), 2, TimeUnit.HOURS);
                } else {
                    roles = Arrays.asList(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(userRoleKey)).split(REGEX));
                }
            } catch (Exception e) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(out));
                log.error("redis缓存错误: {}", out.toString());
            } finally {
                //locker.unlock();
            }
            roles.remove(STRING_NULL);
            for (String str : permissionName) {
                if (roles.contains(str)) {
                    return pjp.proceed();
                }
            }
            throw new CommonException(ErrorCode.PERMISSION_FAILED);
        }
    }
}
