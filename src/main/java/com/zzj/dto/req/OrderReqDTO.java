package com.zzj.dto.req;

import lombok.Data;

import java.util.List;

/**
 * @author zx
 */
@Data
public class OrderReqDTO {

    private Long id;
    private String date;
    private Long lineType;
    private Long userId;
    private String driverId;
    private String attendTime;
    private String offTime;
    private Double kilometer;
    private Double workHour;
    private Long schedule;
    private Long tableId;
    private List<OrderDetailReqDTO> list;
}
