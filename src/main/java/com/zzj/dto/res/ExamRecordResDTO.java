package com.zzj.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zx
 */
@Data
@ApiModel
public class ExamRecordResDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "记录ID")
    private Long reId;

    @ApiModelProperty(value = "记录日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    private Date reDate;

    @ApiModelProperty(value = "占比")
    private Double rePercent;

    @ApiModelProperty(value = "题数")
    private Integer reCount;

    @ApiModelProperty(value = "错误数")
    private Integer reCountError;


}
