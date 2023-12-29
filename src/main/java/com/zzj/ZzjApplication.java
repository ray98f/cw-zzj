package com.zzj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zx
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@MapperScan("com/zzj/mapper")
@EntityScan("com/zzj/entity")
@EnableAsync
@ServletComponentScan("com.zzj.config.filter")
public class ZzjApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZzjApplication.class, args);
	}

}
