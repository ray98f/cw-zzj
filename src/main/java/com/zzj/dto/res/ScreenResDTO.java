package com.zzj.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zx
 */
@Data
@ApiModel
public class ScreenResDTO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "司机1id")
    private String newDriverInfoId;

    @ApiModelProperty(value = "司机1名")
    private String driverName;

    @ApiModelProperty(value = "司机1出勤情况 1 正常 2 迟到")
    private Integer driverFlag;

    @ApiModelProperty(value = "司机2id")
    private String assistantDriverId;

    @ApiModelProperty(value = "司机2名")
    private String assistantDriverName;

    @ApiModelProperty(value = "司机2出勤情况 1 正常 2 迟到")
    private Integer assistantDriverFlag;

    @ApiModelProperty(value = "学员id")
    private String studentDriverId;

    @ApiModelProperty(value = "学员名")
    private String studentDriverName;

    @ApiModelProperty(value = "学员出勤情况 1 正常 2 迟到")
    private Integer studentDriverFlag;

    private String crId;
    private String crName;
    @ApiModelProperty(value = "出勤时间")
    private String attentime;
    private String offtime;
    private String lineId;
    private String lineName;
    @ApiModelProperty(value = "出勤地点")
    private String attenplace;
    @ApiModelProperty(value = "接车车次")
    private String trainName;
    @ApiModelProperty(value = "上下行 1 上行 2 下行")
    private String trainNumberType;


}
