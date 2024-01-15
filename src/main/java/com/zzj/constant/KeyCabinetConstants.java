package com.zzj.constant;

/**
 * 钥匙柜常量
 * @author  Ray
 * @version 1.0
 * @date 2024/01/12
 */
public interface KeyCabinetConstants {

    /**
     * 接口返回标识
     */
    String RESULT_CODE = "resultCode";

    /**
     * 接口返回成功标识
     */
    String RESULT_CODE_SUCCESS = "1";

    /**
     * 接口返回信息
     */
    String RESULT_MESSAGE = "message";

    /**
     * 接口返回数据
     */
    String RESULT_DATA = "data";

    /**
     * 获取token接口地址
     */
    String GET_TOKEN_URL = "/login/logintoken";

    /**
     * 钥匙柜token字段
     */
    String ACCESS_TOKEN = "accessToken";

    /**
     * 获取钥匙取还记录信息接口地址
     */
    String GET_KEY_RECORDS_URL = "/keyrecords/records_query";
}
