package com.zzj.dto.req;

import lombok.Data;

/**
 * description:
 *
 * @author zhangxin
 * @version 1.0
 * @date 2023/12/11 17:16
 */
@Data
public class LoginLogResDTO {
    private String userType;
    private Long userId;
    private String authType;
    private String authAccount;
    private String ipAddress;
    private String userAgent;
    private Integer isSuccess;
}
