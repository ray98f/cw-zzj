package com.zzj.dto.req;

import lombok.Data;

/**
 * @author zx
 */
@Data
public class InRecord {
    public String DepartmentId;           //部门id(必填)
    public Integer TerminalNumber;            //终端机编号
    public String KeyName;                //钥匙名称
    public String UserNumber;                //工号
    public String UserName;               //人员名称
    public String State;                  //状态  1--正常，2--非法
    public String StartTime;              //开始时间(必填)
    public String EndTime;                //结束时间(必填)
    public Integer PageIndex;                 //当前页数(必填)
    public Integer PageSize;                  //每页条数(必填)

}
