package com.zzj.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.zzj.constant.CommonConstants;
import com.zzj.dto.req.*;
import com.zzj.dto.res.*;
import com.zzj.enums.ErrorCode;
import com.zzj.exception.CommonException;
import com.zzj.mapper.UserAccountMapper;
import com.zzj.mapperDM.DmUserAccountMapper;
import com.zzj.mapperEip.EipUserAccountMapper;
import com.zzj.service.UserAccountService;
import com.zzj.shiro.CurrentLoginUser;
import com.zzj.utils.CrytogramUtil;
import com.zzj.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author frp
 */
@Service
@Slf4j
@Transactional
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private EipUserAccountMapper eipUserAccountMapper;

    @Autowired
    private DmUserAccountMapper dmUserAccountMapper;

    @Override
    public String getUser(UserLoginReqDTO userLoginReqDTO) {
        SystemUserResDTO user = null;
        switch (userLoginReqDTO.getLoginType()){
            case CommonConstants.LOGIN_TYPE_NORMAL:
                user = loginByPwd(userLoginReqDTO);
                break;
            case CommonConstants.LOGIN_TYPE_CARD:
                user = loginByCard(userLoginReqDTO);
                break;
            case CommonConstants.LOGIN_TYPE_FINGER:
                user = loginByFinger(userLoginReqDTO);
                break;
            default:
                break;
        }
        if (Objects.isNull(user)) {
            throw new CommonException(ErrorCode.USER_OR_PWD_ERROR);
        }
        CurrentLoginUser currentLoginUser = new CurrentLoginUser(user.getUserId(),user.getUserName(),user.getUserViewName(),user.getIamUserId());
        return TokenUtil.createSimpleToken(currentLoginUser);
    }

    @Override
    public UserAccountDetailResDTO userDetail(CurrentLoginUser currentLoginUser) {
        //
        UserAccountDetailResDTO user = dmUserAccountMapper.getUserDetail(currentLoginUser.getPersonId());
        if (Objects.isNull(user)) {
            throw new CommonException(ErrorCode.RESOURCE_NOT_EXIST);
        }

        List<UserPositionResDTO> positionList = dmUserAccountMapper.getUserPosition(currentLoginUser.getUserId());
        StringBuilder positionStr = new StringBuilder(",");
        for (UserPositionResDTO position : positionList){
            positionStr.append(position.getPositionName()).append(",");
        }
        user.setPositionNameStr(positionStr.toString());
        user.setPositionList(positionList);

        // 查询当日三交三问已通过的记录
        Integer recordCount = dmUserAccountMapper.getPassRecordCount(currentLoginUser.getUserId());
        if (recordCount > 0) {
            user.setThreeCheck(1);
        } else {
            user.setThreeCheck(0);
        }
        return user;
    }

    @Override
    public List<ExamResDTO> getExamList(CurrentLoginUser currentLoginUser) {

        List<ExamResDTO> list = new ArrayList<>();
        List<ExamConfigDetailResDTO> cnfList = dmUserAccountMapper.getExamConf(currentLoginUser.getUserId());
        if(cnfList.size() == 0){
            list = dmUserAccountMapper.getExamList(CommonConstants.DEFAULT_EXAM_COUNT + 1 );
        } else {
            for(ExamConfigDetailResDTO cnf : cnfList){
                list.addAll(dmUserAccountMapper.getExamByCnf(cnf.getExamtypeId(),cnf.getExamnum()+1));
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamRecordReqDTO saveExam(CurrentLoginUser currentLoginUser,Map<String, List<String>> paraMap) {
        Object[] examList = paraMap.get("examList").toArray();
        Object[] examAnswer = paraMap.get("examAnswer").toArray();
        Object[] examCorrect = paraMap.get("examCorrect").toArray();
        int errCount = 0;
        List<RecordDetailReqDTO> recordDetailList = new ArrayList<>();
        for(int i = 0; i<examList.length; i++){
            RecordDetailReqDTO recordDetail = new RecordDetailReqDTO();
            recordDetail.setRelExamId(Integer.parseInt(examList[i].toString()));
            recordDetail.setRelAnswer(examAnswer[i].toString());
            if((examAnswer[i]+"").equals(examCorrect[i]+"")){
                recordDetail.setRelCorrect(1);
            }else{
                errCount += 1;
                recordDetail.setRelCorrect(0);
            }
            recordDetailList.add(recordDetail);
        }
        ExamRecordReqDTO er = new ExamRecordReqDTO();
        er.setUserId(currentLoginUser.getUserId());
        er.setReCount(examList.length);
        er.setReCountError(errCount);
        er.setRePercent((double)(Math.round((examList.length - errCount)*100/examList.length)/100.0));

        try{
            Integer rec = dmUserAccountMapper.addRecord(er);
            if(rec > 0){
                dmUserAccountMapper.addRecordDetail(recordDetailList,er.getReId());
            }
        }catch (Exception e){
            throw new CommonException(ErrorCode.INSERT_ERROR);
        }
        return er;
    }

    @Override
    public DutyDetailResDTO getDutyInfo(CurrentLoginUser currentLoginUser) {

        DutyDetailResDTO res = dmUserAccountMapper.getDutyInfo(currentLoginUser.getUserId());
        if(res == null || CommonConstants.OFF_CR_NAME.equals(res.getCrName())){
            //未排班
            throw new CommonException(ErrorCode.RESOURCE_NOT_EXIST);
        }
        res.setAttentime(timeChange(res.getAttentime()));
        res.setOfftime(timeChange(res.getOfftime()));
        List<DmAttendQuitResDTO> workList = dmUserAccountMapper.getAttendQuit(res.getId());
        if(workList.size() == 0){
            res.setAttendFlag(0);
            res.setQuitFlag(0);
        }else if(workList.size() == 1){
            res.setAttendFlag(1);
            res.setQuitFlag(0);
        }else {
            res.setAttendFlag(1);
            res.setQuitFlag(1);
        }
        res.setWorkRecord(workList);

        return res;
    }

    @Override
    public DutyDetailResDTO getNextDutyInfo(CurrentLoginUser currentLoginUser, String recDate) {
        try{
            DutyDetailResDTO res = dmUserAccountMapper.getNextDutyInfo(currentLoginUser.getPersonId(),recDate);
            if(res != null){
                res.setAttentime(timeChange(res.getAttentime()));
                res.setOfftime(timeChange(res.getOfftime()));
            }
            return res;
        }catch (Exception e) {
            throw new CommonException(ErrorCode.RESOURCE_NOT_EXIST);
        }
    }

    @Override
    public String dutyOn(CurrentLoginUser currentLoginUser,AttendQuitReqDTO attendQuitReqDTO) {

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:m:s");
        String attendTime = sdf.format(new Date());
        attendQuitReqDTO.setId(snowflakeGenerator.next());
        attendQuitReqDTO.setWorkType("1");
        attendQuitReqDTO.setUserId(currentLoginUser.getUserId());
        attendQuitReqDTO.setActionTime(attendTime);
        Integer res = dmUserAccountMapper.dutyOn(attendQuitReqDTO);
        if(res > 0){
            return attendTime;
        }
        return "";
    }

    @Override
    public String dutyOff(CurrentLoginUser currentLoginUser,AttendQuitReqDTO attendQuitReqDTO) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:m:s");
        String quitTime = sdf.format(new Date());
        attendQuitReqDTO.setId(snowflakeGenerator.next());
        attendQuitReqDTO.setWorkType("2");
        attendQuitReqDTO.setActionTime(quitTime);
        attendQuitReqDTO.setUserId(currentLoginUser.getUserId());
        Integer res = dmUserAccountMapper.dutyOff(attendQuitReqDTO);
        if(res > 0){
            return quitTime;
        }
        return "";
    }

    @Override
    public List<TrainScheduleDTO> orderInit(CurrentLoginUser currentLoginUser, String stringRunList) {
        if(StringUtils.isEmpty(stringRunList)){
            return null;
        }
        List<String> trainIds = Arrays.asList(stringRunList.split(CommonConstants.TRAIN_SPLIT));
        List<TrainScheduleDTO> res = dmUserAccountMapper.getTrainSchedules(trainIds);
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveOrder(OrderReqDTO orderInfo) {

        try{
            orderInfo.setId(snowflakeGenerator.next());
            Integer res = dmUserAccountMapper.saveOrderInfo(orderInfo);
            if(res > 0){
                dmUserAccountMapper.addOrderDetail(orderInfo.getList(),orderInfo.getId());
            }
        }catch (Exception e){
            throw new CommonException(ErrorCode.INSERT_ERROR);
        }
        return orderInfo.getId()+"";
    }

    @Override
    public List<TrainStationResDTO> getStationList(String trainNum) {
        return dmUserAccountMapper.getTrainStation(trainNum);
    }

    @Override
    public List<UserFaceFeatureResDTO> featureList() {
        return dmUserAccountMapper.featureList();
    }

    private SystemUserResDTO loginByPwd(UserLoginReqDTO userLoginReqDTO){
        SystemUserResDTO user = eipUserAccountMapper.getUserByName(userLoginReqDTO.getUsername());
        if (StringUtils.isEmpty(userLoginReqDTO.getUsername()) || StringUtils.isEmpty(userLoginReqDTO.getPassword())
                || Objects.isNull(user)) {
            return null;
        }
        String userPwd = CrytogramUtil.encrypt(CrytogramUtil.decrypt64(userLoginReqDTO.getPassword()).toString(),"MD5")+"";
        if(!userPwd.equals(user.getUserPassword())){
            return null;
        }
        IamUserResDTO iamUser = dmUserAccountMapper.getUserByName(userLoginReqDTO.getUsername());
        user.setIamUserId(iamUser.getId());
        return user;
    }


    private SystemUserResDTO loginByCard(UserLoginReqDTO userLoginReqDTO){
        //TODO 根据物理卡号 获取员工信息  从数据共享平台申请调用 员工卡信息接口  目前还没有 20231124
        //query api userNo
        String uuid = userLoginReqDTO.getCardNo();

        try{
            SysUserCardResDTO user1 = dmUserAccountMapper.getUserByCard(uuid);
            SystemUserResDTO user = eipUserAccountMapper.getUserByName(user1.getUserNo());
            IamUserResDTO iamUser = dmUserAccountMapper.getUserByName(user1.getUserNo());
            user.setIamUserId(iamUser.getId());
            return user;
        }catch (Exception e){
            throw new CommonException(ErrorCode.RESOURCE_NOT_EXIST);
        }

    }

    private SystemUserResDTO loginByFinger(UserLoginReqDTO userLoginReqDTO){

        return null;
    }

    private String timeChange(String timeNum){
        String timeStr = String.format("%06d",Integer.parseInt(timeNum));
        String newTimeStr = "";
        if(timeStr.length() == 6){
            newTimeStr += timeStr.substring(0,2) + ":";
            newTimeStr += timeStr.substring(2,4) + ":";
            newTimeStr += timeStr.substring(4,6);
        }
        return newTimeStr;
    }

}
