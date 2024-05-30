package com.zzj.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.zzj.dto.req.*;
import com.zzj.dto.res.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserAccountMapper {

    SystemUserResDTO getUserByName(String userName);

    SystemUserResDTO getUserByCard(String cardUid);

    UserAccountDetailResDTO getUserDetail(Integer userId);

    @SqlParser(filter = true)
    List<ExamResDTO> getExamList();

    Integer addRecord(ExamRecordReqDTO examRecordReqDTO);

    Integer addRecordDetail(List<RecordDetailReqDTO> list,Integer reId);

    DutyDetailResDTO getDutyInfo(Integer userId);

    DutyDetailResDTO getNextDutyInfo(Integer userId,String recDate);

    AttendQuitResDTO getAttendQuit(String crId);

    Integer dutyOn(AttendQuitReqDTO attendQuitReqDTO);

    Integer dutyOff(AttendQuitReqDTO attendQuitReqDTO);

    List<TrainScheduleDTO> getTrainSchedules(List<String> trainIds);

    Integer saveOrderInfo(OrderReqDTO orderInfo);

    Integer addOrderDetail(List<OrderDetailReqDTO> list,Long orderId);

}
