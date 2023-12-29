package com.zzj.mapperEip;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.zzj.dto.req.*;
import com.zzj.dto.res.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EipUserAccountMapper {


    SystemUserResDTO getUserByName(String userName);

    SystemUserResDTO getUserByCard(String userName);

    SystemUserResDTO getUserByFinger(String userName);

    UserAccountDetailResDTO getUserDetail(Integer userId);

    Integer getPassRecordCount(Integer userId);

    @SqlParser(filter = true)
    List<ExamResDTO> getExamList();

    Integer addRecord(ExamRecordReqDTO examRecordReqDTO);

    Integer addRecordDetail(List<RecordDetailReqDTO> list,Integer reId);

    DutyDetailResDTO getDutyInfo(Integer userId);

    DutyDetailResDTO getNextDutyInfo(Integer userId,String recDate);

    AttendQuitResDTO getAttendQuit(String crId);

    Integer dutyOn(AttendQuitReqDTO attendQuitReqDTO);

    Integer dutyOff(AttendQuitReqDTO attendQuitReqDTO);

    List<TrainScheduleDTO> getTrainSchedules(List<String> trainIds,Integer tableId);

    Integer saveOrderInfo(OrderReqDTO orderInfo);

    Integer addOrderDetail(List<OrderDetailReqDTO> list,String orderId);
}
