package com.zzj.constant;

/**
 * 常量
 *
 * @author zhangxin
 * @version 1.0
 * @date 2021/8/2 10:31
 */
public interface CommonConstants {

     String COMMA = ",";

     String UNKNOWN = "unknown";

    /**
     * 删除
     */
    String IS_DEL = "1";


    /**
     * 正常
     */
    String STATUS_NORMAL = "0";

    /**
     * 禁用
     */
    String STATUS_BAN = "1";

    /**
     * webservice成功标记
     */
    String WSDL_SUCCESS = "0";
    Integer BATCH_EXECUTOR_SIZE = 100;
    String CURRENT_USER = "currentUser";

    /**
     * 编码
     */
    String UTF8 = "UTF-8";

    /**
     * JSON 资源
     */
    String CONTENT_TYPE = "application/json; charset=utf-8";


    /**
     * 成功标记
     */
    Integer SUCCESS = 0;

    /**
     * 失败标记
     */
    Integer FAIL = 401;

    /**
     * 当前页
     */
    String CURRENT = "current";

    /**
     * size
     */
    String SIZE = "size";

    /**
     * 人脸识别成功标记
     */
    Integer COMPARE_FACE_SUCCESS = 1;

    /**
     * 人脸识别失败标记
     */
    Integer COMPARE_FACE_FAIL = 2;
    String BUCKET_POLICY_DEFAULT ="";
    String BUCKET_POLICY_READ ="read";
    String BUCKET_POLICY_WRITE ="write";
    String BUCKET_POLICY_READ_WRITE ="read-write";
    String SUPPLIER_FLAG = "W";
    String DEFAULT_EIP_ROLE = "8a84ad9161daefdf0161daf074500001";
    String DEFAULT_PWD = "M+JUjNeeSsoLITbNtRqPjA==";
    String DEFAULT_USER_TYPE = "3";
    String SUPP_USER_TYPE = "2";
    String ADMIN_USER_TYPE = "1";

    String TRAIN_SPLIT = ",";
    String LOGIN_TYPE_NORMAL = "1";
    String LOGIN_TYPE_CARD = "2";
    String LOGIN_TYPE_FACE = "3";
    String OFF_CR_NAME = "休";
    Integer DEFAULT_EXAM_COUNT = 3;
    Integer SIX = 6;

    String DEF_TENANT_ID = "RXTX";
    String DUTY_OFF_CR_NAME_CHECK = "晚";
    String KEY_BOX_RESULT_CODE = "resultCode";
    String KEY_BOX_RESULT_DATA = "data";
    String KEY_BOX_RECORD_DATA = "RecData";
    String KEY_BOX_TOKEN = "accessToken";
    String KEY_BOX_RES_CODE = "1";
    String DATE_START = " 00:00:00";
    String DATE_END = " 59:59:00";
    Integer DEFAULT_PAGE_SIZE = 10;
    Integer DEFAULT_PAGE_INDEX = 1;
}
