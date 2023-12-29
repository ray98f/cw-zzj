package com.zzj.dto.req;

import lombok.Data;

/**
 * 用户排班信息实体类
 * @author  Ray
 * @version 1.0
 * @date 2023/12/16
 */
@Data
public class UserDutyReqDTO {
    /**
     * 车次id
     */
    private String train;
    /**
     * 车次名称
     */
    private String trainName;
    /**
     * 起始站id
     */
    private String stationStart;
    /**
     * 起始站名称
     */
    private String stationStartName;
    /**
     * 终点站id
     */
    private String stationEnd;
    /**
     * 终点站名称
     */
    private String stationEndName;

}
