package com.zzj.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zx
 */
@Data
@ApiModel
public class TrainScheduleDTO {

    @ApiModelProperty(value = "记录ID")
    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "车辆ID")
    private Long trainId;

    @ApiModelProperty(value = "车辆车次")
    private String trainName;

    @ApiModelProperty(value = "车站")
    private String stationName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "车站id")
    private Long stationId;

    @ApiModelProperty(value = "车站类型1始发站 2终点站")
    private String stationType;

    @ApiModelProperty(value = "到站时间")
    private String arrive;

    @ApiModelProperty(value = "离站时间")
    private String depart;

}
