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
public class SysUserCardResDTO {


    @ApiModelProperty(value = "工号")
    private String userNo;

    @ApiModelProperty(value = "uuid")
    private String cardUuid;





}
