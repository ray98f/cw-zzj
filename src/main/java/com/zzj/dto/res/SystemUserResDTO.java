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
public class SystemUserResDTO {

    //用户ID
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long iamUserId;

    @ApiModelProperty(value = "工号")
    private String userId;

    @ApiModelProperty(value = "名字")
    private String userViewName;

    @ApiModelProperty(value = "账号")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String userPassword;





}
