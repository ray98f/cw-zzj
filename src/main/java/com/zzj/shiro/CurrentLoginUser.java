package com.zzj.shiro;

import lombok.Data;

import java.io.Serializable;

/**
 * description:
 *
 * @author zhangxin
 * @version 1.0
 * @date 2021/8/3 11:34
 */
@Data
public class CurrentLoginUser implements Serializable {

    private String personId;

    private String personNo;

    private String personName;

    private Long userId;

    public CurrentLoginUser() {
    }

    public CurrentLoginUser(String personId, String personNo, String personName, Long userId) {
        this.personId = personId;
        this.personNo = personNo;
        this.personName = personName;
        this.userId = userId;
    }
}
