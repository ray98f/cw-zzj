package com.zzj.entity;

import lombok.Data;

/**
 * @author cy
 * @Date 2020-02-19 09:34
 */
@Data
public class Resource {
    /**
     * 匹配访问路径
     * 多个用','隔离
     */
    private String pathPatterns;

    /**
     * 资源路径
     * 多个用','隔离
     */
    private String resourceLocations;
}

