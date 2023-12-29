package com.zzj.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.zzj.mapperDM", sqlSessionTemplateRef  = "zzj2SqlSessionTemplate")
public class DataSource2Config {

    @Autowired
    private MybatisPlusConfig mybatisPlusConfig;

    @Bean(name = "zzj2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.zzj2")
    public DataSource testDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "zzj2SqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("zzj2DataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setMultipleResultSetsEnabled(true);
//        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
        configuration.setLogImpl(org.apache.ibatis.logging.nologging.NoLoggingImpl.class);
        bean.setConfiguration(configuration);
        bean.setPlugins(new PaginationInterceptor[]{mybatisPlusConfig.paginationInterceptor()});
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapperDM/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "zzj2TransactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("zzj2DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "zzj2SqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("zzj2SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}