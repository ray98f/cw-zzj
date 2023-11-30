package com.zzj.config.filter;

import com.zzj.config.RequestHeaderContext;
import com.zzj.enums.ErrorCode;
import com.zzj.enums.TokenStatus;
import com.zzj.exception.CommonException;
import com.zzj.shiro.CurrentLoginUser;
import com.zzj.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * jwt过滤器
 *
 * @author frp
 */
@Slf4j
@Component
public class JwtFilter implements Filter {
    private static final String JSESSIONID_FIX = ";jsessionid";

    @Value("${excluded.pages}")
    private String[] pages;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        if(uri.contains(JSESSIONID_FIX)){
            String newUri = uri.substring(0, uri.indexOf(";"));
            String jsessionid = uri.replace(newUri+";jsessionid=","");
            httpRequest.getSession();
            Cookie cookie = new Cookie("JSESSIONID", jsessionid);
            httpResponse.addCookie(cookie);
            httpResponse.sendRedirect(newUri);
            return;
        }

        if (Arrays.asList(pages).contains(httpRequest.getRequestURI()) || httpRequest.getRequestURI().contains("/uploadFile/") ||
                httpRequest.getRequestURI().contains("/css/")  || httpRequest.getRequestURI().contains("/fonts/")
                || httpRequest.getRequestURI().contains("/js/") || httpRequest.getRequestURI().contains("/static/")
                || httpRequest.getRequestURI().contains("/vendor/")|| httpRequest.getRequestURI().contains("/open/")) {
            chain.doFilter(httpRequest, httpResponse);
        } else {
            String token = httpRequest.getHeader("Authorization");
            if (token == null || StringUtils.isBlank(token)) {
                request.setAttribute("filter.error", new CommonException(ErrorCode.AUTHORIZATION_EMPTY));
                request.getRequestDispatcher("/error/exthrow").forward(request, response);
                return;
            }
            TokenStatus tokenStatus = TokenUtil.verifySimpleToken(token);
            switch (Objects.requireNonNull(tokenStatus)) {
                //有效
                case VALID:
                    CurrentLoginUser simpleTokenInfo = TokenUtil.getSimpleTokenInfo(token);
                    new RequestHeaderContext.RequestHeaderContextBuild().user(simpleTokenInfo).build();
                    httpRequest.setAttribute("tokenInfo", simpleTokenInfo);
                    chain.doFilter(httpRequest, httpResponse);
                    break;
                //过期
                case EXPIRED:
                    request.setAttribute("filter.error", new CommonException(ErrorCode.AUTHORIZATION_IS_OVERDUE));
                    request.getRequestDispatcher("/error/exthrow").forward(request, response);
                    break;
                //无效
                default:
                    request.setAttribute("filter.error", new CommonException(ErrorCode.AUTHORIZATION_INVALID));
                    request.getRequestDispatcher("/error/exthrow").forward(request, response);
                    break;
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("jwtFilter init ...");
    }

    @Override
    public void destroy() {
        log.info("jwtFilter destroy ...");
    }
}