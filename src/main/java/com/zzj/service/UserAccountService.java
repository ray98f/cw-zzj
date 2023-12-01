package com.zzj.service;

import com.zzj.dto.req.AttendQuitReqDTO;
import com.zzj.dto.req.ExamRecordReqDTO;
import com.zzj.dto.req.OrderReqDTO;
import com.zzj.dto.req.UserLoginReqDTO;
import com.zzj.dto.res.*;
import com.zzj.shiro.CurrentLoginUser;

import java.util.List;
import java.util.Map;

/**
 * @author zx
 */
public interface UserAccountService {


    String getUser(UserLoginReqDTO userLoginReqDTO);

    UserAccountDetailResDTO userDetail(CurrentLoginUser currentLoginUser);

    List<ExamResDTO> getExamList(CurrentLoginUser currentLoginUser);

    ExamRecordReqDTO saveExam(CurrentLoginUser currentLoginUser, Map<String,List<String>> paraMap);

    DutyDetailResDTO getDutyInfo(CurrentLoginUser currentLoginUser);
    DutyDetailResDTO getNextDutyInfo(CurrentLoginUser currentLoginUser,String recDate);

    String dutyOn(CurrentLoginUser currentLoginUser,AttendQuitReqDTO attendQuitReqDTO);

    String dutyOff(CurrentLoginUser currentLoginUser,AttendQuitReqDTO attendQuitReqDTO);

    List<TrainScheduleDTO> orderInit(CurrentLoginUser currentLoginUser, String stringRunList);

    String saveOrder(OrderReqDTO orderInfo);

    List<TrainStationResDTO> getStationList(String trainNum);

    List<UserFaceFeatureResDTO> featureList();

}
