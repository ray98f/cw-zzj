package com.zzj.dto.req;

import lombok.Data;

/**
 * @author zx
 */
@Data
public class ExamRecordReqDTO {

    private Integer reId;
    private Long userId;
    private Double rePercent;
    private Integer reCount;
    private Integer reCountError;
}
