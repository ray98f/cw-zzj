package com.zzj.mapperDM;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.zzj.dto.req.*;
import com.zzj.dto.res.*;
import io.swagger.models.auth.In;
import jdk.nashorn.internal.parser.Token;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DmUserAccountMapper {


    IamUserResDTO getUserByName(String userName);

    SysUserCardResDTO getUserByCard(String uuid);

    String getUserByFace(String face);

    SystemUserResDTO getUserByFinger(String userName);

    UserAccountDetailResDTO getUserDetail(String userNo);

    List<UserPositionResDTO> getUserPosition(Long userId);

    Integer getPassRecordCount(Long userId, Integer type);

    List<ExamResDTO> getExamByCnf(Long typeId ,Integer rowCount);
    List<ExamResDTO> getExamList(Integer rowCount);

    List<ExamConfigDetailResDTO> getExamConf(Long userId);
    Integer addRecord(ExamRecordReqDTO examRecordReqDTO);

    Integer addRecordDetail(List<RecordDetailReqDTO> list,Long reId);

    DutyDetailResDTO getDutyInfo(Long userId);

    DutyDetailResDTO getNextDutyInfo(Long userId,String recDate);

    /**
     * 获取派班人员
     * @param classType 排班类型
     * @return 派班人员姓名
     */
    String getDispatchUser(String classType);

    /**
     * 获取出退勤情况
     * @param crId 排班id
     * @param userId 用户id
     * @return
     */
    List<DmAttendQuitResDTO> getAttendQuit(Long crId, Long userId);

    Integer dutyOn(AttendQuitReqDTO attendQuitReqDTO);

    Integer dutyOff(AttendQuitReqDTO attendQuitReqDTO);

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
    TrainScheduleDTO getTrainScheduleByTrainIdAndStationId(Long trainId, Long stationId);

    /**
     * 根据用户排班信息获取详细排班信息
     * @param list 用户排班信息列表
     * @return 排班信息
     */
    List<TrainScheduleDTO> getTrainSchedulesDetail(List<UserDutyReqDTO> list);

    List<TrainScheduleDTO> getTrainSchedules(List<String> list);

    /**
     * 判断人员在指定日期是否已派单
     * @param date 日期
     * @param driverId 工号
     * @return 是否已报单 0 否 1 是
     */
    Integer getHadSaveOrder(String date, String driverId);

    Integer saveOrderInfo(OrderReqDTO orderInfo);

    Integer addOrderDetail(List<OrderDetailReqDTO> list,Long orderId);

    List<TrainStationResDTO> getTrainStation(String trainNum);

    List<UserFaceFeatureResDTO> featureList();

    Integer addUserFace(String userNo,List<String> faceList);

    Integer updateFace(String userNo);

    Integer cleanUserCardTable();

    CheckDutyResDTO checkDutyInfo(Long userId,Long id);

    List<UserKeyStoreRecordResDTO> getKeyRecord(CheckDutyResDTO duty);

    KeyCabinetResDTO getKeyCabinetInfo(String boxNum,String keyNum);

    Integer insertKeyRecord(UserKeyStoreRecordResDTO userKeyStoreRecord);
}
