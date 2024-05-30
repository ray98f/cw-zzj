package com.zzj.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author zx
 */
@Data
@ApiModel
public class DutyDetailResDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "ID")
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "交路ID")
    private Long crId;

    @ApiModelProperty(value = "交路名")
    private String crName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "主控司机")
    private Long newDriverInfoId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "副控司机")
    private Long assistantDriverId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "学员司机")
    private Long studentDriverId;

    @ApiModelProperty(value = "出勤日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd",
            timezone = "GMT+8"
    )
    private Date recDate;

    @ApiModelProperty(value = "计划出勤时间")
    private String attentime;

    @ApiModelProperty(value = "计划退勤时间")
    private String offtime;

    @ApiModelProperty(value = "计划车次")
    private String reptrain;

    private String stringruntrain;

    private String startRunCrossingroad;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long tableId;

    private String tableName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long lineId;

    private String lineName;

    private String miles;

    private String whours;

    @ApiModelProperty(value = "计划出勤地点")
    private String attenplace;

    @ApiModelProperty(value = "出勤标记")
    private Integer attendFlag;

    @ApiModelProperty(value = "退勤标记")
    private Integer quitFlag;

    @ApiModelProperty(value = "出退勤记录")
    private AttendQuitResDTO attendQuitRecord;

    @ApiModelProperty(value = "出退勤记录")
    private List<DmAttendQuitResDTO> workRecord;

    /**
     * 班次类型
     */
    private String classType;

    /**
     * 当日是否排班（0 排班 1 未排班）
     * "孕","年","产","病","疗","事","育","独","丧","婚","护","调","休"
     */
    private Integer isWork;

    /**
     * 次日是否排班（0 排班 1 未排班）
     * "孕","年","产","病","疗","事","育","独","丧","婚","护","调","休"
     */
    private Integer isNextWork;

    /**
     * 交路类型id
     */
    private Long crossingRoadTypeId;

    /**
     * 交路类型名称
     */
    private String crossingRoadTypeName;

    /**
     * 是否为特殊交路类型（0 特殊 1 不特殊）
     * 特殊包含 指导司机、司机长、调车
     */
    private Integer isSpecialType;

    /**
     * 排班人员
     */
    private String dispatchUser;

    /**
     * 始发车次
     */
    private String firstTrain;

    /**
     * 钥匙柜信息
     */
    private String keyCabinet;


}
