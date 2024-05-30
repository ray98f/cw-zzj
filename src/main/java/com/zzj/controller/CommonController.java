package com.zzj.controller;

import com.zzj.constant.CommonConstants;
import com.zzj.dto.DataResponse;
import com.zzj.dto.JXResDTO;
import com.zzj.dto.KQResDTO;
import com.zzj.dto.req.UserLoginReqDTO;
import com.zzj.service.CommonService;
import com.zzj.service.UserAccountService;
import com.zzj.utils.IpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * description:
 *
 * @author frp
 * @version 1.0
 * @date 2021/10/20 17:06
 */
@CrossOrigin
@Slf4j
@RestController
@Api(tags = "公共分类")
@Validated
public class CommonController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private CommonService commonService;

    @ApiOperation(value = "登出")
    @GetMapping(value = "/logout")
    public DataResponse<String> logout() {

        return null;
    }

    @ApiOperation(value = "登录")
    @PostMapping(value = "/login")
    public DataResponse<String> login(@RequestBody UserLoginReqDTO userLoginReqDTO, HttpServletRequest request) {
        String Token = userAccountService.getUser(userLoginReqDTO, request);
        return DataResponse.of(Token);
    }

    //faceCompare 逻辑有点问题
    @GetMapping("/user/faceCompare")
    @ApiOperation(value = "人脸比对")
    public DataResponse<String> featureList(@RequestParam String id, HttpServletRequest request) {
        UserLoginReqDTO userLoginReqDTO = new UserLoginReqDTO();
        userLoginReqDTO.setLoginType(CommonConstants.LOGIN_TYPE_FACE);
        userLoginReqDTO.setUsername(id);
        String Token = userAccountService.getUser(userLoginReqDTO, request);
        return DataResponse.of(Token);
    }

    @ApiOperation(value = "获取客户端IP地址")
    @GetMapping(value = "/ipAdd")
    public DataResponse<String> getIP(HttpServletRequest httpServletRequest) {
        String ipAddress = IpUtils.getIpAddr(httpServletRequest);
        return DataResponse.of(ipAddress);
    }

    @ApiOperation(value = "考勤汇总")
    @GetMapping(value = "/kq")
    public DataResponse<List<KQResDTO>> kq(@RequestParam(required = false) String date) {
        return DataResponse.of(commonService.queryKq(date));
    }

    @ApiOperation(value = "绩效汇总")
    @GetMapping(value = "/jx")
    public DataResponse<JXResDTO> jx() {
        return DataResponse.of(commonService.queryJx());
    }
}
