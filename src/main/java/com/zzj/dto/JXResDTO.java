package com.zzj.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author zx
 */
@Data
@ApiModel
public class JXResDTO {

    private String sysCode;
    private String sysName;
    private List<JXMonthResDTO> monthVoList;
    private List<JXYearResDTO> yearVoList;

}
