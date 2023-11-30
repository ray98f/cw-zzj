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
    private String userViewName;
    private String driverId;
    private String attendTime;
    private String offTime;
    private Double kilometer;
    private Double workHour;
    private Long schedule;
    private List<OrderDetailReqDTO> list;
}
