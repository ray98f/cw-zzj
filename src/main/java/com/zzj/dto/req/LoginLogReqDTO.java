package com.zzj.dto.req;

import lombok.Data;

/**
 * 一体机登录日志类
 * @author  Ray
 * @version 1.0
 * @date 2023/12/13
 */
@Data
public class LoginLogReqDTO {
    /**
     * 用户id
     */
    private Long iamUserId;
    /**
     * 登录类型
     */
    private String loginType;
    /**
     * 用户名
     */
    private String userName;
    /**
     * IP地址
     */
    private String ip;
    /**
     * 使用代理
     */
    private String userAgent;
    /**
     * id
     */
    private Long id;
    /**
     * 登录传参json
     */
    private String loginJson;

}
