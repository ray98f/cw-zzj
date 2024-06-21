package com.zzj.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author zx
 */
@Data
@ApiModel
public class JXMonthResDTO {

    private String driverName;
    private String driverNo;
    private String positionName;
    private String dateTime;
    private String totalScore;
    private String monthStarStatus;
    private String typeName;
    private String sysCode;
    private String systemName;

}
