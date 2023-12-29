package com.zzj.dto.req;

import lombok.Data;

/**
 * @author zx
 */
@Data
public class ExamRecordReqDTO {

    private Long reId;
    private Long userId;
    private Double rePercent;
    private Integer reCount;
    private Integer reCountError;
}
