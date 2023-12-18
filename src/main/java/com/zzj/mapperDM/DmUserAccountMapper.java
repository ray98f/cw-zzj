package com.zzj.mapperDM;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.zzj.dto.req.*;
import com.zzj.dto.res.*;
import io.swagger.models.auth.In;
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

    List<DmAttendQuitResDTO> getAttendQuit(Long crId);

    Integer dutyOn(AttendQuitReqDTO attendQuitReqDTO);

    Integer dutyOff(AttendQuitReqDTO attendQuitReqDTO);

    /**
     * 根据用户排班信息获取详细排班信息
     * @param list 用户排班信息列表
     * @return 排班信息
     */
    List<TrainScheduleDTO> getTrainSchedulesDetail(List<UserDutyReqDTO> list);

    List<TrainScheduleDTO> getTrainSchedules(List<String> list);

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
