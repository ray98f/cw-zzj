package com.zzj.dto.res;

import lombok.Data;

/**
 * 车次信息类
 * @author  Ray
 * @version 1.0
 * @date 2023/12/21
 */
@Data
public class TrainsResDTO {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 线路id
     */
    private String trainName;
}
