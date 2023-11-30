package com.zzj.dto.res;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zx
 */
@Data
@ApiModel
public class UserAccountDetailResDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "账号")
    private String userName;

    @ApiModelProperty(value = "名字")
    private String userViewName;

    @ApiModelProperty(value = "角色")
    private String userRole;

    @ApiModelProperty(value = "角色名")
    private String roleName;

    @ApiModelProperty(value = "考试通过标记")
    private Integer threeCheck;

    @ApiModelProperty(value = "考试记录")
    private ExamRecordResDTO examRecord;

    @ApiModelProperty(value = "排班")
    private DutyDetailResDTO dutyDetail;

    private String teamId;

    @JSONField(defaultValue = "")
    private String teamName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "所属线路")
    private Long lineId;

    @ApiModelProperty(value = "所属线路")
    private String lineName;

    @ApiModelProperty(value = "岗位名称")
    private String positionNameStr;

    @ApiModelProperty(value = "岗位列表")
    private List<UserPositionResDTO> positionList;

}
