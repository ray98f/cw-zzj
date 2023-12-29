package com.zzj.controller.user;

import com.zzj.annotation.CurrUser;
import com.zzj.dto.DataResponse;
import com.zzj.dto.res.UserAccountDetailResDTO;
import com.zzj.service.UserAccountService;
import com.zzj.shiro.CurrentLoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zx
 */
@Slf4j
@RestController
@RequestMapping("/finger")
@Api(tags = "用户指纹管理")
@Validated
public class FingerController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/addFinger")
    @ApiOperation(value = "新增")
    public DataResponse<UserAccountDetailResDTO> addFinger(@CurrUser CurrentLoginUser currentLoginUser) {
        return null;
    }

    @GetMapping("/deleteFinger")
    @ApiOperation(value = "删除")
    public DataResponse<UserAccountDetailResDTO> user(@CurrUser CurrentLoginUser currentLoginUser) {
        return null;
    }
}
