package com.zzj.dto.req;

import lombok.Data;

import java.util.Date;

/**
 * @author zx
 */
@Data
public class AttendQuitReqDTO {

    private Long id;
    private Long newSchedulingId;
    private Long userId;
    private String workType;
    private String actionTime;
    private Integer alcoholTest;
    private String alcoholTestStatus;
    private Double alcoholResult;
    /**
     * 钥匙柜信息
     */
    private String keyCabinet;

}
