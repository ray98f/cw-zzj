package com.zzj.dto.res;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zx
 */
@Data
@ApiModel
public class UserKeyStoreRecordResDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "ID")
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "钥匙ID")
    private Long latticeId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "存储时间")
    private Date useTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "排班ID")
    private Long schedulingId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "交路ID")
    private Long crossingId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "ID")
    private Long cabinetId;


    private String keyCabinetCode;

    private String latticeCode;
}
