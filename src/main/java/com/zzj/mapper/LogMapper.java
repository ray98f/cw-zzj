package com.zzj.mapper;

import com.zzj.dto.req.LoginLogReqDTO;
import com.zzj.dto.res.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 日志管理
 * @author  Ray
 * @version 1.0
 * @date 2023/12/13
 */
@Mapper
@Repository
public interface LogMapper {

    /**
     * 登录日志录入
     * @param loginLogReqDTO 登录日志信息
     */
    void insertLoginLog(LoginLogReqDTO loginLogReqDTO);

}
