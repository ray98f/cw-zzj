package com.zzj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzj.dto.PageReqDTO;
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
    DutyDetailResDTO getNextDutyInfo(CurrentLoginUser currentLoginUser);

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
     * @param trainId 车辆id
     * @return 车站列表
     */
    List<StationsResDTO> listStations(Long lineId, Long trainId);

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

    String keyCabinetTest(CurrentLoginUser currentLoginUser, String offTime, Integer type, String day);

    /**
     * 大屏接口
     * @param pageReqDTO 分页参数
     * @return 大屏信息
     */
    Page<ScreenResDTO> screen(PageReqDTO pageReqDTO);

}
