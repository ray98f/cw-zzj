package com.zzj.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 报单车次详情
 * @author  Ray
 * @version 1.0
 * @date 2023/12/25
 */
@Data
@ApiModel
public class TrainScheduleResDTO {

    /**
     * 车辆ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long trainId;

    /**
     * 车辆车次
     */
    private String trainName;

    /**
     * 始发站id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long startStationId;

    /**
     * 始发站名称
     */
    private String startStationName;

    /**
     * 始发站到站时间
     */
    private String startArrive;

    /**
     * 始发站离站时间
     */
    private String startDepart;

    /**
     * 终点站id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long endStationId;

    /**
     * 终点站名称
     */
    private String endStationName;

    /**
     * 终点站到站时间
     */
    private String endArrive;

    /**
     * 终点站离站时间
     */
    private String endDepart;

}
