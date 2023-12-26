package com.zzj.service;

import com.zzj.dto.req.AttendQuitReqDTO;
import com.zzj.dto.req.ExamRecordReqDTO;
import com.zzj.dto.req.OrderReqDTO;
import com.zzj.dto.req.UserLoginReqDTO;
import com.zzj.dto.res.*;
import com.zzj.shiro.CurrentLoginUser;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zx
 */
public interface UserAccountService {


    String getUser(UserLoginReqDTO userLoginReqDTO, HttpServletRequest request);

    UserAccountDetailResDTO userDetail(CurrentLoginUser currentLoginUser);

    List<ExamResDTO> getExamList(CurrentLoginUser currentLoginUser);

    ExamRecordReqDTO saveExam(CurrentLoginUser currentLoginUser, Map<String,List<String>> paraMap);

    DutyDetailResDTO getDutyInfo(CurrentLoginUser currentLoginUser);
    DutyDetailResDTO getNextDutyInfo(CurrentLoginUser currentLoginUser,String recDate);

    String dutyOn(CurrentLoginUser currentLoginUser,AttendQuitReqDTO attendQuitReqDTO);

    String dutyOff(CurrentLoginUser currentLoginUser,AttendQuitReqDTO attendQuitReqDTO);

    /**
     * 根据账号所属线路获取当天车次列表
     * @param lineId 线路id
     * @return 车次列表
     */
    List<TrainsResDTO> listTrains(Long lineId);

    /**
     * 根据账号所属线路获取车站列表
     * @param lineId 线路id
     * @return 车站列表
     */
    List<StationsResDTO> listStations(Long lineId);

    /**
     * 根据列车和站点查询列车信息
     * @param trainId 列车id
     * @param stationId 站点id
     * @return 列车信息
     */
    TrainScheduleDTO getTrainSchedule(Long trainId, Long stationId);

    List<TrainScheduleResDTO> orderInit(String stringRunList);

    void saveOrder(OrderReqDTO orderInfo);

    List<TrainStationResDTO> getStationList(String trainNum);

    List<UserFaceFeatureResDTO> featureList();

    Integer faceRegister(CurrentLoginUser currentLoginUser,List<HashMap<String,Object>> list);

    CheckKeyStoreResDTO checkKeyStore(CurrentLoginUser currentLoginUser,HashMap<String,Object> map);

    // void getKeyRecord();

}
