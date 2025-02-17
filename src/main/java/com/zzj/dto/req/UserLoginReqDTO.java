package com.zzj.dto.req;

import lombok.Data;

/**
 * @author zx
 */
@Data
public class UserLoginReqDTO {

    private String username;
    private String password;
    private String faceTpl;
    private String cardNo;
    private String loginType;
}
