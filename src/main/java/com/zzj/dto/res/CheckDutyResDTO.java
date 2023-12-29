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
public class CheckDutyResDTO {

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

    private String newDriverNo;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "副控司机")
    private Long assistantDriverId;

    private String assistantDriverNo;


    @ApiModelProperty(value = "出勤日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd",
            timezone = "GMT+8"
    )
    private Date recDate;






}
