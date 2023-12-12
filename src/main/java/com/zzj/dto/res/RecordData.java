package com.zzj.dto.res;

import lombok.Data;

/**
 * description:
 *
 * @author zhangxin
 * @version 1.0
 * @date 2023/12/7 9:14
 */
@Data
public class RecordData {

    public String id;                   //唯一ID
    public Integer terminalNumber;            //终端机编号
    public Integer boxNumber;               //钥匙柜编号
    public Integer keyNumber;               //钥匙编号
    public String keyId;                //钥匙ID
    public String keyName;           //钥匙名称
    public String keyCard;              //钥匙卡号
    public String takeKeyTime;          //取钥匙时间
    public String takeUserNumber;          //取钥匙人员编号
    public String takeUserName;         //取钥匙人员名称
    public String takeAuxUserName;      //取钥匙辅助认证人员名称
    public String keyTakeRecordID;               //取钥匙的唯一ID
    public String returnKeyTime;        //还钥匙时间
    public String returnUserNumber;        //还钥匙人员编号
    public String eturnUserName;       //还钥匙人员名称
    public String returnAuxUserName;    //还钥匙辅助认证人员名称
    public Integer overTimeCloseDoor;       //是否关门超时  0-否，1-是
    public String doorOpenTime; //开门时间
    public String doorCloseTime; //关门时间
    public Integer recordType; //安卓端用取钥匙结果（0正常，1非法，2紧急）
    public Integer returnFlag; //钥匙是否归还 0-否，1-是
    public Integer overTimeReturn; //是否超时归还  0-否，1-是
    public String departmentId;      //部门id
    public String state;               //Windows端取还状态（1正常，2非法）
    public String Remarks;               //备注
    
}
