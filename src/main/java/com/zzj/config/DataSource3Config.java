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
@MapperScan(basePackages = "com.zzj.mapperEip", sqlSessionTemplateRef  = "zzj3SqlSessionTemplate")
public class DataSource3Config {

    @Autowired
    private MybatisPlusConfig mybatisPlusConfig;

    @Bean(name = "zzj3DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.zzj3")
    public DataSource testDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "zzj3SqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("zzj3DataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setMultipleResultSetsEnabled(true);
        configuration.setLogImpl(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
        bean.setConfiguration(configuration);
        bean.setPlugins(new PaginationInterceptor[]{mybatisPlusConfig.paginationInterceptor()});
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapperEip/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "zzj3TransactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("zzj3DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "zzj3SqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("zzj3SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}