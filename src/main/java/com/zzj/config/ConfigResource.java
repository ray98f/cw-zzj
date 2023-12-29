package com.zzj.config;

import com.zzj.entity.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author cy
 * @Date 2020-02-19 09:41
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "spring.static")
public class ConfigResource {
    List<Resource> resources;
}

