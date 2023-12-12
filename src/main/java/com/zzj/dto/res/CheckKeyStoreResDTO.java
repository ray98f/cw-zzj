package com.zzj.dto.res;

import lombok.Data;

import java.util.List;

/**
 * description:
 *
 * @author zhangxin
 * @version 1.0
 * @date 2023/12/7 10:03
 */
@Data
public class CheckKeyStoreResDTO {

    private Integer checkRes;
    private CheckDutyResDTO checkDutyInfo;
    private UserKeyStoreRecordResDTO userKeyStoreRecordRes;
}
