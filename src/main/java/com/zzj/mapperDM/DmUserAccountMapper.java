package com.zzj.mapperDM;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.zzj.dto.req.*;
import com.zzj.dto.res.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DmUserAccountMapper {


    IamUserResDTO getUserByName(String userName);

    SysUserCardResDTO getUserByCard(String uuid);

    SystemUserResDTO getUserByFinger(String userName);

    UserAccountDetailResDTO getUserDetail(String userNo);

    List<UserPositionResDTO> getUserPosition(Long userId);

    Integer getPassRecordCount(Long userId);

    List<ExamResDTO> getExamByCnf(Long typeId ,Integer rowCount);
    List<ExamResDTO> getExamList(Integer rowCount);

    List<ExamConfigDetailResDTO> getExamConf(Long userId);
    Integer addRecord(ExamRecordReqDTO examRecordReqDTO);

    Integer addRecordDetail(List<RecordDetailReqDTO> list,Integer reId);

    DutyDetailResDTO getDutyInfo(Long userId);

    DutyDetailResDTO getNextDutyInfo(String userId,String recDate);

    List<DmAttendQuitResDTO> getAttendQuit(Long crId);

    Integer dutyOn(AttendQuitReqDTO attendQuitReqDTO);

    Integer dutyOff(AttendQuitReqDTO attendQuitReqDTO);

    List<TrainScheduleDTO> getTrainSchedules(List<String> list);

    Integer saveOrderInfo(OrderReqDTO orderInfo);

    Integer addOrderDetail(List<OrderDetailReqDTO> list,Long orderId);

    List<TrainStationResDTO> getTrainStation(String trainNum);

    List<UserFaceFeatureResDTO> featureList();
}
