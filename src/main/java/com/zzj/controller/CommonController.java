package com.zzj.controller;

import com.zzj.dto.DataResponse;
import com.zzj.dto.req.UserLoginReqDTO;
import com.zzj.service.UserAccountService;
import com.zzj.utils.IpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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


    @ApiOperation(value = "登出")
    @GetMapping(value = "/logout")
    public DataResponse<String> logout() {

        return null;
    }

    @ApiOperation(value = "登录")
    @PostMapping(value = "/login")
    public DataResponse<String> login(@RequestBody UserLoginReqDTO userLoginReqDTO) {
        String Token = userAccountService.getUser(userLoginReqDTO);
        return DataResponse.of(Token);
    }

    @ApiOperation(value = "获取客户端IP地址")
    @GetMapping(value = "/ipAdd")
    public DataResponse<String> getIP(HttpServletRequest httpServletRequest) {
        String ipAddress = IpUtils.getIpAddr(httpServletRequest);
        return DataResponse.of(ipAddress);
    }




}
