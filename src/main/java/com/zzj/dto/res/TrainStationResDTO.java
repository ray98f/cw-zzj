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
public class TrainStationResDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "")
    private Long trainId;

    @ApiModelProperty(value = "车次号")
    private String trainNum;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "车站ID")
    private Long stationId;

    @ApiModelProperty(value = "车站名")
    private String stationName;


}
