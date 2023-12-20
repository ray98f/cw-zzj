package com.zzj.dto.req;

import lombok.Data;

/**
 * @author zx
 */
@Data
public class RecordDetailReqDTO {

    private Long relExamId;
    private String relAnswer;
    private Integer relCorrect;
    private Integer relReId;
}
