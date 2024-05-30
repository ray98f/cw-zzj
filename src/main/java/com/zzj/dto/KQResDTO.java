package com.zzj.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author zx
 */
@Data
@ApiModel
public class KQResDTO {

    private String id;
    private String updateTime;
    private String iamUserId;
    private String quarter;
    private String lineId;
    private String positionId;
    private String firstMonthWorkHour;
    private String secondMonthWorkHour;
    private String thirdMonthWorkHour;
    private String firstMonthHolidayHour;
    private String secondMonthHolidayHour;
    private String thirdMonthHolidayHour;
    private String sickLeave;
    private String absenceLeave;
    private String annualLeave;
    private String otherHolidays;
    private String abnormalClocking;
    private String lateArrival;
    private String earlyDeparture;
    private String absenteeism;
    private String bigNightShift;
    private String littleNightShift;
    private String otherWorkHour;
    private String workType;
    private String analysisYear;
    private String groupId;
    private String auditId;
    private String iamUserRealname;
    private String iamUserUserNum;
    private String positionName;
    private String overtimeWorkHours;
    private String compensatoryDays;
    private String bereavementLeaveDays;
    private String maternityLeaveDays;
    private String marriageLeaveDays;
    private String workInjuryDays;
    private String familyVisitDays;
    private String businessTripDays;
    private String officialTripDays;
    private String quarterlyTotalHours;
    private String quarterlyLegalHolidayHours;

}
