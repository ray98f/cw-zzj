package com.zzj.dto.res;

import lombok.Data;

/**
 * 钥匙取还记录信息类（外部提供）
 * @author  Ray
 * @version 1.0
 * @date 2024/01/12
 */
@Data
public class KeyCabinetResDTO {
    /**
     * 唯一ID
     */
    private String ID;
    /**
     * 终端机编号
     */
    private Integer TerminalNumber;
    /**
     * 钥匙柜编号
     */
    private Integer BoxNumber;
    /**
     * 钥匙编号
     */
    private Integer KeyNumber;
    /**
     * 钥匙ID
     */
    private String KeyId;
    /**
     * 钥匙名称
     */
    private String KeyName;
    /**
     * 钥匙卡号
     */
    private String KeyCard;
    /**
     * 取钥匙时间
     */
    private String TakeKeyTime;
    /**
     * 取钥匙人员编号
     */
    private String TakeUserNumber;
    /**
     * 取钥匙人员名称
     */
    private String TakeUserName;
    /**
     * 取钥匙辅助认证人员名称
     */
    private String TakeAuxUserName;
    /**
     * 取钥匙的唯一ID
     */
    private String KeyTakeRecordID;
    /**
     * 还钥匙时间
     */
    private String ReturnKeyTime;
    /**
     * 还钥匙人员编号
     */
    private String ReturnUserNumber;
    /**
     * 还钥匙人员名称
     */
    private String ReturnUserName;
    /**
     * 还钥匙辅助认证人员名称
     */
    private String ReturnAuxUserName;
    /**
     * 是否关门超时  0-否，1-是
     */
    private Boolean overTimeCloseDoor;
    /**
     * 开门时间
     */
    private String doorOpenTime;
    /**
     * 关门时间
     */
    private String doorCloseTime;
    /**
     * 安卓端用取钥匙结果（0正常，1非法，2紧急）
     */
    private Integer recordType;
    /**
     * 钥匙是否归还 0-否，1-是
     */
    private Boolean returnFlag;
    /**
     * 是否超时归还  0-否，1-是
     */
    private Boolean overTimeReturn;
    /**
     * 部门id
     */
    private String DepartmentId;
    /**
     * Windows端取还状态（1正常，2非法）
     */
    private String State;
    /**
     * 备注
     */
    private String Remarks;

}
