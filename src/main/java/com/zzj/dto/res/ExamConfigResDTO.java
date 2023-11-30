package com.zzj.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * description:
 *
 * @author zhangxin
 * @version 1.0
 * @date 2023/11/20 16:49
 */
@Data
public class ExamConfigResDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "岗位ID")
    private Long positionId;

    @ApiModelProperty(value = "岗位编码")
    private Long LineId;

    @ApiModelProperty(value = "考题数")
    private Integer examcount;

    @ApiModelProperty(value = "考题类型")
    private List<ExamConfigDetailResDTO> cnfList;

}
