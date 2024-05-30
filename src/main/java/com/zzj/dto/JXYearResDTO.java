package com.zzj.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * @author zx
 */
@Data
@ApiModel
public class JXYearResDTO {

    private String driverName;
    private String driverNo;
    private String positionName;
    private String dateTime;
    private String totalScore;
    private String typeName;
    private String result;
    private HashMap<String,Object> valueMap;

}
