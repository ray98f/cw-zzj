package com.zzj.dto.req;

import lombok.Data;

/**
 * @author zx
 */
@Data
public class OrderDetailReqDTO {

    private String id;
    private Long reportId;
    private Long trainNum;
    private Long startStation;
    private String startTime;
    private Long endStation;
    private String endTime;

}
