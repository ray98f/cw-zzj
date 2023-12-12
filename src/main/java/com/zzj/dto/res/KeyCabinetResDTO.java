package com.zzj.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zx
 */
@Data
@ApiModel
public class KeyCabinetResDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "ID")
    private Long keyId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "钥匙柜ID")
    private Long boxId;

    @ApiModelProperty(value = "钥匙编号")
    private String latticeCode;

    @ApiModelProperty(value = "展示名称")
    private String latticeName;

    @ApiModelProperty(value = "钥匙状态")
    private String latticeStatus;

    @ApiModelProperty(value = "钥匙柜编号")
    private String keyCabinetCode;

    @ApiModelProperty(value = "钥匙柜名称")
    private String keyCabinetName;

}
