package com.zzj.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import com.zzj.utils.Constants;

import java.sql.Timestamp;

/**
 * description:
 *
 * @author chentong
 * @version 1.0
 * @date 2020/12/14 16:54
 */
@Configuration
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        //默认未删除
        setFieldValByName("isDeleted", Constants.DATA_NOT_DELETED, metaObject);
        //创建时间默认当前时间
        setFieldValByName("createdTime", new Timestamp(System.currentTimeMillis()), metaObject);

        //更新时间默认当前时间
        setFieldValByName("updatedTime", new Timestamp(System.currentTimeMillis()), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {

        setFieldValByName("updatedTime", new Timestamp(System.currentTimeMillis()), metaObject);
    }

}
