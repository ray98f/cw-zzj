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
public class ExamResDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "题号")
    private Long examId;

    @ApiModelProperty(value = "题目")
    private String examText;

    @ApiModelProperty(value = "选择项A")
    private String optionA;

    @ApiModelProperty(value = "选择项B")
    private String optionB;

    @ApiModelProperty(value = "选择项C")
    private String optionC;

    @ApiModelProperty(value = "选择项D")
    private String optionD;

    @ApiModelProperty(value = "选择项E")
    private String optionE;

    @ApiModelProperty(value = "选择项F")
    private String optionF;

    @ApiModelProperty(value = "正确")
    private String answerCorrect;

    /**
     * 题目类型
     */
    private String examType;

    /**
     * 题目类型名称
     */
    private String examTypeName;

}
