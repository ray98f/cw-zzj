package com.zzj.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zx
 */
@Data
@ApiModel
public class IamUserResDTO {

    //用户ID
    private Long id;

    @ApiModelProperty(value = "工号")
    private String userNum;





}
