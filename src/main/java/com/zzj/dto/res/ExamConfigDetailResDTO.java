package com.zzj.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * description:
 *
 * @author zhangxin
 * @version 1.0
 * @date 2023/11/20 16:49
 */
@Data
public class ExamConfigDetailResDTO {


    @ApiModelProperty(value = "类型ID")
    private Long examtypeId;

    @ApiModelProperty(value = "考题数")
    private Integer examnum;

}
