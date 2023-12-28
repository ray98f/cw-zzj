package com.zzj.dto.res;

import lombok.Data;

/**
 * 车站信息类
 * @author  Ray
 * @version 1.0
 * @date 2023/12/21
 */
@Data
public class StationsResDTO {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 车站名
     */
    private String stationName;
    /**
     * 车站编号
     */
    private String stationCode;
    /**
     * 线路id
     */
    private Long lineId;
}
