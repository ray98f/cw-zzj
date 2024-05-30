package com.zzj.service.impl;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.page.PageMethod;
import com.zzj.constant.CommonConstants;
import com.zzj.constant.KeyCabinetConstants;
import com.zzj.constant.OcmConstants;
import com.zzj.dto.PageReqDTO;
import com.zzj.dto.req.*;
import com.zzj.dto.res.*;
import com.zzj.enums.ErrorCode;
import com.zzj.exception.CommonException;
import com.zzj.mapper.LogMapper;
import com.zzj.mapperDM.DmUserAccountMapper;
import com.zzj.mapperEip.EipUserAccountMapper;
import com.zzj.service.UserAccountService;
import com.zzj.shiro.CurrentLoginUser;
import com.zzj.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author frp
 */
@Service
@Slf4j
@Transactional
public class UserAccountServiceImpl implements UserAccountService {

    @Value("${ocm.base-url}")
    private String ocmBaseUrl;
    @Value("${key-cabinet.base-url}")
    private String keyCabinetBaseUrl;

    @Value("${key-cabinet.name}")
    private String keyCabinetName;

    @Value("${key-cabinet.pwd}")
    private String keyCabinetPwd;

    @Value("${key-cabinet.dept}")
    private String keyCabinetDept;

    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    @Autowired
    private EipUserAccountMapper eipUserAccountMapper;

    @Autowired
    private DmUserAccountMapper dmUserAccountMapper;

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private RestTemplate restTemplate;

    private final String[] DUTY_REST = new String[] {"孕", "年", "产", "病", "疗", "事", "育", "独", "丧", "婚", "护", "调", "休"};

    private final String[] CROSSING_ROAD_TYPE = new String[] {"指导司机", "司机长", "调车"};

    @Override
    public String getUser(UserLoginReqDTO userLoginReqDTO, HttpServletRequest request) {
        SystemUserResDTO user = null;
        String loginType = "";
        switch (userLoginReqDTO.getLoginType()) {
            case CommonConstants.LOGIN_TYPE_NORMAL:
                user = loginByPwd(userLoginReqDTO);
                loginType = "ZZJ_PWD";
                break;
            case CommonConstants.LOGIN_TYPE_CARD:
                user = loginByCard(userLoginReqDTO);
                loginType = "ZZJ_CARD";
                break;
            case CommonConstants.LOGIN_TYPE_FACE:
                user = loginByFace(userLoginReqDTO);
                loginType = "ZZJ_FACE";
                break;
            default:
                break;
        }
        if (Objects.isNull(user)) {
            throw new CommonException(ErrorCode.USER_OR_PWD_ERROR);
        }
        insertLoginLog(user, userLoginReqDTO, loginType, request);
        CurrentLoginUser currentLoginUser = new CurrentLoginUser(user.getUserId(), user.getUserName(), user.getUserViewName(), user.getIamUserId());
        return TokenUtil.createSimpleToken(currentLoginUser);
    }

    /**
     * 登录日志录入
     *
     * @param user            用户信息
     * @param userLoginReqDTO 用户登录信息
     * @param loginType       登录类型
     * @param request         请求体
     */
    private void insertLoginLog(SystemUserResDTO user, UserLoginReqDTO userLoginReqDTO, String loginType, HttpServletRequest request) {
        String loginJson = JSONObject.toJSONString(userLoginReqDTO);
        log.info("=====登录信息JSON:" + loginJson);
        LoginLogReqDTO loginLogReqDTO = new LoginLogReqDTO();
        loginLogReqDTO.setIamUserId(user.getIamUserId());
        loginLogReqDTO.setLoginType(loginType);
        loginLogReqDTO.setUserName(user.getUserName());
        loginLogReqDTO.setIp(IpUtils.getIpAddr(request));
        loginLogReqDTO.setUserAgent(request.getHeader("User-Agent"));
        loginLogReqDTO.setLoginJson(loginJson);
        loginLogReqDTO.setId(snowflakeGenerator.next());
        logMapper.insertLoginLog(loginLogReqDTO);
    }

    @Override
    public UserAccountDetailResDTO userDetail(CurrentLoginUser currentLoginUser) {
        UserAccountDetailResDTO user = dmUserAccountMapper.getUserDetail(currentLoginUser.getPersonId());
        if (Objects.isNull(user)) {
            throw new CommonException(ErrorCode.USER_INFO_NOT_EXIST);
        }
        user.setDutyDetail(getDutyInfo(currentLoginUser));
        List<UserPositionResDTO> positionList = dmUserAccountMapper.getUserPosition(currentLoginUser.getUserId());
        StringBuilder positionStr = new StringBuilder(",");
        for (UserPositionResDTO position : positionList) {
            positionStr.append(position.getPositionName()).append(",");
        }
        user.setPositionNameStr(positionStr.toString());
        user.setPositionList(positionList);

        // 查询当日三交三问的记录
        Integer recordCount = dmUserAccountMapper.getPassRecordCount(currentLoginUser.getUserId(), null);
        if (recordCount > 0 || user.getDutyDetail().getAttendFlag() == 1) {
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
        if (cnfList.isEmpty()) {
            list = dmUserAccountMapper.getExamList(CommonConstants.DEFAULT_EXAM_COUNT + 1);
        } else {
            for (ExamConfigDetailResDTO cnf : cnfList) {
                list.addAll(dmUserAccountMapper.getExamByCnf(cnf.getExamtypeId(), cnf.getExamnum() + 1));
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamRecordReqDTO saveExam(CurrentLoginUser currentLoginUser, Map<String, List<String>> paraMap) {
        Object[] examList = paraMap.get("examList").toArray();
        Object[] examAnswer = paraMap.get("examAnswer").toArray();
        Object[] examCorrect = paraMap.get("examCorrect").toArray();
        int errCount = 0;
        List<RecordDetailReqDTO> recordDetailList = new ArrayList<>();
        for (int i = 0; i < examList.length; i++) {
            RecordDetailReqDTO recordDetail = new RecordDetailReqDTO();
            recordDetail.setRelExamId(Long.parseLong(examList[i].toString()));
            recordDetail.setRelAnswer(examAnswer[i].toString());
            if ((examAnswer[i] + "").equals(examCorrect[i] + "")) {
                recordDetail.setRelCorrect(1);
            } else {
                errCount += 1;
                recordDetail.setRelCorrect(0);
            }
            recordDetailList.add(recordDetail);
        }
        ExamRecordReqDTO er = new ExamRecordReqDTO();
        er.setReId(snowflakeGenerator.next());
        er.setUserId(currentLoginUser.getUserId());
        er.setReCount(examList.length);
        er.setReCountError(errCount);
        er.setRePercent((Math.round((float) ((examList.length - errCount) * 100) / examList.length) / 100.0) * 100);

        try {
            Integer result = dmUserAccountMapper.addRecord(er);
            if (result > 0) {
                dmUserAccountMapper.addRecordDetail(recordDetailList, er.getReId());
            }
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INSERT_ERROR);
        }
        return er;
    }

    @Override
    public DutyDetailResDTO getDutyInfo(CurrentLoginUser currentLoginUser) {
        List<DutyDetailResDTO> lastDutyInfoList = dmUserAccountMapper.getNextDutyInfo(currentLoginUser.getUserId(), -1);
        List<DutyDetailResDTO> nowDutyInfosList = dmUserAccountMapper.getDutyInfo(currentLoginUser.getUserId());
        DutyDetailResDTO lastDutyInfo = getNearlyDutyInfo(lastDutyInfoList);
        DutyDetailResDTO nowDutyInfo = getNearlyDutyInfo(nowDutyInfosList);
        // 根据当前时间是否已过1点判断
        DutyDetailResDTO dutyInfo = (DateUtils.dutyTimeDetermine() ? lastDutyInfo : nowDutyInfo);
        if (dutyInfo == null) {
            throw new CommonException(ErrorCode.DUTY_INFO_NOT_EXIST);
        }
        if (!Objects.isNull(dutyInfo.getCrName())) {
            if (Arrays.stream(DUTY_REST).anyMatch(rest -> dutyInfo.getCrName().equals(rest))) {
                throw new CommonException(ErrorCode.DUTY_REST);
            }
        }
        if (!Objects.isNull(dutyInfo.getCrossingRoadTypeName())) {
            if (Arrays.stream(CROSSING_ROAD_TYPE).anyMatch(type -> dutyInfo.getCrossingRoadTypeName().contains(type))) {
                dutyInfo.setIsSpecialType(0);
            } else {
                dutyInfo.setIsSpecialType(1);
            }
        }
        List<UserDutyReqDTO> trains = JSONArray.parseArray(dutyInfo.getStartRunCrossingroad(), UserDutyReqDTO.class);
        if (!Objects.isNull(trains) && !trains.isEmpty()) {
            dutyInfo.setFirstTrain(trains.get(0).getTrainName());
        }
        dutyInfo.setDispatchUser(dmUserAccountMapper.getDispatchUser(dutyInfo.getClassType()));
        setDutyTime(dutyInfo);
        setWorkState(dutyInfo, currentLoginUser);
        return dutyInfo;
    }

    @Override
    public DutyDetailResDTO getNextDutyInfo(CurrentLoginUser currentLoginUser) {
        try {
            int i = 1;
            List<DutyDetailResDTO> dutyInfoList = dmUserAccountMapper.getNextDutyInfo(currentLoginUser.getUserId(), i);
            DutyDetailResDTO dutyInfo = getNearlyDutyInfo(dutyInfoList);
            while (Objects.isNull(dutyInfo) || !Objects.isNull(Objects.requireNonNull(dutyInfo).getCrName())) {
                DutyDetailResDTO finalDutyInfo = dutyInfo;
                if (com.zzj.utils.StringUtils.isNotNull(finalDutyInfo) && Arrays.stream(DUTY_REST).noneMatch(rest -> finalDutyInfo.getCrName().equals(rest))) {
                    break;
                }
                i++;
                dutyInfoList = dmUserAccountMapper.getNextDutyInfo(currentLoginUser.getUserId(), i);
                dutyInfo = getNearlyDutyInfo(dutyInfoList);
            }
            setDutyTime(dutyInfo);
            List<UserDutyReqDTO> trains = JSONArray.parseArray(dutyInfo.getStartRunCrossingroad(), UserDutyReqDTO.class);
            if (!Objects.isNull(trains) && !trains.isEmpty()) {
                dutyInfo.setFirstTrain(trains.get(0).getTrainName());
            }
            return dutyInfo;
        } catch (Exception e) {
            throw new CommonException(ErrorCode.NEXT_DUTY_INFO_ERROR);
        }
    }

    /**
     * 获取出勤时间最近的排班
     * @param list 排班列表
     * @return 排班信息
     */
    private DutyDetailResDTO getNearlyDutyInfo(List<DutyDetailResDTO> list) {
        String now = new SimpleDateFormat("HHmmss").format(new Date());
        if (com.zzj.utils.StringUtils.isEmpty(list)) {
            return null;
        } else if (com.zzj.utils.StringUtils.isNotEmpty(list) && list.size() == 1) {
            return list.get(0);
        } else {
            try {
                return list.stream().min(Comparator.comparingInt(data -> Math.abs(Integer.parseInt(data.getAttentime()) - Integer.parseInt(now))))
                        .orElseThrow(() -> new CommonException(ErrorCode.DUTY_INFO_ERROR));
            } catch (Exception e) {
                throw new CommonException(ErrorCode.DUTY_INFO_ERROR);
            }
        }
    }

    /**
     * 填充排班出退勤时间
     * @param dutyInfo 排班信息
     */
    private void setDutyTime(DutyDetailResDTO dutyInfo) {
        if (com.zzj.utils.StringUtils.isNotEmpty(dutyInfo.getAttentime())) {
            dutyInfo.setAttentime(DateUtils.timeChange(dutyInfo.getAttentime()));
        } else {
            dutyInfo.setAttentime("");
        }
        if (com.zzj.utils.StringUtils.isNotEmpty(dutyInfo.getOfftime())) {
            dutyInfo.setOfftime(DateUtils.timeChange(dutyInfo.getOfftime()));
        } else {
            dutyInfo.setOfftime("");
        }
    }

    /**
     * 填充出退勤状态及出退勤信息
     * @param dutyInfo 排班信息
     * @param currentLoginUser 用户信息
     */
    private void setWorkState(DutyDetailResDTO dutyInfo, CurrentLoginUser currentLoginUser) {
        String day = (DateUtils.dutyTimeDetermine() ? DateUtils.getYesterday() : DateUtils.getToday());
        // 根据当前时间是否已过1点判断
        List<DmAttendQuitResDTO> workList = dmUserAccountMapper.getAttendQuit(dutyInfo.getId(), currentLoginUser.getUserId(), (DateUtils.dutyTimeDetermine() ? 1 : 2));
        // 交路类型包含司机长、指导司机、调车且班次类型为早班  早班不出勤
        if (!Objects.isNull(dutyInfo.getCrossingRoadTypeName())) {
            String EARLY_CLASS_TYPE = "1";
            if (Arrays.stream(CROSSING_ROAD_TYPE).anyMatch(type -> dutyInfo.getCrossingRoadTypeName().contains(type)) && EARLY_CLASS_TYPE.equals(dutyInfo.getClassType())) {
                workList.add(new DmAttendQuitResDTO());
            }
        }
        if (workList.isEmpty()) {
            dutyInfo.setAttendFlag(0);
            dutyInfo.setQuitFlag(0);
        } else if (workList.size() == 1) {
            dutyInfo.setAttendFlag(1);
            dutyInfo.setQuitFlag(0);
        } else {
            dutyInfo.setAttendFlag(1);
            dutyInfo.setQuitFlag(1);
        }
        // 根据出勤状态获取钥匙柜信息
        dutyInfo.setKeyCabinet(getKeyCabinet(currentLoginUser, dutyInfo.getOfftime(), dutyInfo.getAttendFlag() == 0 ? 1 : 2, day));
        dutyInfo.setWorkRecord(workList);
    }

    @Override
    public String dutyOn(CurrentLoginUser currentLoginUser, AttendQuitReqDTO attendQuitReqDTO) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:m:s");
        String attendTime = sdf.format(new Date());
        attendQuitReqDTO.setId(snowflakeGenerator.next());
        attendQuitReqDTO.setWorkType("1");
        attendQuitReqDTO.setUserId(currentLoginUser.getUserId());
        attendQuitReqDTO.setActionTime(attendTime);
        Integer res = dmUserAccountMapper.dutyOn(attendQuitReqDTO);
        if (res > 0) {
            return attendTime;
        }
        return "";
    }

    @Override
    public String dutyOff(CurrentLoginUser currentLoginUser, AttendQuitReqDTO attendQuitReqDTO) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:m:s");
        String quitTime = sdf.format(new Date());
        attendQuitReqDTO.setId(snowflakeGenerator.next());
        attendQuitReqDTO.setWorkType("2");
        attendQuitReqDTO.setActionTime(quitTime);
        attendQuitReqDTO.setUserId(currentLoginUser.getUserId());
        Integer res = dmUserAccountMapper.dutyOff(attendQuitReqDTO);
        if (res > 0) {
            return quitTime;
        }
        return "";
    }

    @Override
    public List<TrainsResDTO> listTrains(Long lineId) {
        return dmUserAccountMapper.listTrains(lineId);
    }

    @Override
    public List<StationsResDTO> listStations(Long lineId, Long trainId) {
        return dmUserAccountMapper.listStations(lineId, trainId);
    }

    @Override
    public TrainScheduleDTO getTrainSchedule(Long trainId, Long stationId) {
        return dmUserAccountMapper.getTrainScheduleByTrainIdAndStationId(trainId, stationId);
    }

    @Override
    public List<TrainScheduleResDTO> orderInit(String stringRunList) {
        if (StringUtils.isEmpty(stringRunList)) {
            return null;
        }
        List<UserDutyReqDTO> trains = JSONArray.parseArray(stringRunList, UserDutyReqDTO.class);
        if (Objects.isNull(trains) || trains.isEmpty()) {
            return null;
        }
        List<TrainScheduleDTO> list = dmUserAccountMapper.getTrainSchedulesDetail(trains);
        List<TrainScheduleResDTO> resList = new ArrayList<>();
        if (!Objects.isNull(list) && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if ("2".equals(list.get(i).getStationType())) {
                    continue;
                }
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(i).getTrainId().equals(list.get(j).getTrainId()) && "2".equals(list.get(j).getStationType())) {
                        TrainScheduleResDTO res = getTrainScheduleRes(list, i, j);
                        resList.add(res);
                        break;
                    }
                }
            }
        }
        return resList.stream().sorted(Comparator.comparing(TrainScheduleResDTO::getStartDepart)).collect(Collectors.toList());
    }

    /**
     * 报单数据重组
     * @param list 原始报单数据
     * @param i 外部循环参数
     * @param j 内部循环参数
     * @return TrainScheduleResDTO
     */
    private TrainScheduleResDTO getTrainScheduleRes(List<TrainScheduleDTO> list, int i, int j) {
        TrainScheduleResDTO res = new TrainScheduleResDTO();
        res.setTrainId(list.get(i).getTrainId());
        res.setTrainName(list.get(i).getTrainName());
        res.setStartStationId(list.get(i).getStationId());
        res.setStartStationName(list.get(i).getStationName());
        res.setStartArrive(list.get(i).getArrive());
        res.setStartDepart(list.get(i).getDepart());
        res.setEndStationId(list.get(j).getStationId());
        res.setEndStationName(list.get(j).getStationName());
        res.setEndArrive(list.get(j).getArrive());
        res.setEndDepart(list.get(j).getDepart());
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrder(OrderReqDTO orderInfo) {
        Integer res = dmUserAccountMapper.getHadSaveOrder(orderInfo.getDate(), orderInfo.getDriverId());
        if (res > 0) {
            return;
        }
        try {
            orderInfo.setId(snowflakeGenerator.next());
            if (!Objects.isNull(orderInfo.getOffTime()) && !orderInfo.getOffTime().isEmpty()) {
                orderInfo.setOffTime(DateUtils.getToday() + " " + orderInfo.getOffTime());
            }
            res = dmUserAccountMapper.saveOrderInfo(orderInfo);
            boolean orderInfoEmpty = orderInfo.getList() != null && !orderInfo.getList().isEmpty();
            if (res > 0 && orderInfoEmpty) {
                List<OrderDetailReqDTO> list = orderInfo.getList();
                list = list.stream().filter(OrderDetailReqDTO -> OrderDetailReqDTO.getTrainNum() != null).collect(Collectors.toList());
                List<OrderDetailReqDTO> reqList = new ArrayList<>();
                list.forEach(p -> {
                    if (!reqList.contains(p)) {
                        reqList.add(p);
                    }
                });
                for (OrderDetailReqDTO req : reqList) {
                    req.setStartTime(DateUtils.getToday() + " " + DateUtils.timeChange(req.getStartTime()));
                    req.setEndTime(DateUtils.getToday() + " " + DateUtils.timeChange(req.getEndTime()));
                }
                dmUserAccountMapper.addOrderDetail(reqList, orderInfo.getId());
            }
            sendOrderToOsm(orderInfo);
        } catch (Exception e) {
            log.error("exception message", e);
            throw new CommonException(ErrorCode.INSERT_ERROR);
        }
    }

    @Override
    public List<TrainStationResDTO> getStationList(String trainNum) {
        return dmUserAccountMapper.getTrainStation(trainNum);
    }

    /**
     * 获取钥匙柜信息
     * @param currentLoginUser 用户信息
     * @param offTime 退勤时间
     * @param type 类型 1 出勤 2 退勤
     * @param day 日期
     * @return 钥匙柜信息
     */
    public String getKeyCabinet(CurrentLoginUser currentLoginUser, String offTime, Integer type, String day) {
//        String token = getToken();
//        if (com.zzj.utils.StringUtils.isEmpty(offTime)) {
//            return "";
//        }
//        if (type == 1) {
//            DmAttendQuitResDTO quitRes = dmUserAccountMapper.getQuit(currentLoginUser.getUserId());
//            if (Objects.isNull(quitRes)) {
//                return "";
//            }
//            return quitRes.getKeyCabinetName();
//        }
//        if (type == 2) {
//            String startTime = day + " " + DateUtils.timeChange(offTime);
//            List<KeyCabinetResDTO> list = getKeyCabinetRecords(token, startTime, currentLoginUser.getPersonNo());
//            if (com.zzj.utils.StringUtils.isNotEmpty(list)) {
//                // todo 处理钥匙取还记录信息
//                list = list.stream().sorted(Comparator.comparing(KeyCabinetResDTO::getDoorCloseTime).reversed()).collect(Collectors.toList());
//                return String.valueOf(list.get(0).getBoxNumber());
//            }
//        }
        return "";
    }

    /**
     * 获取钥匙柜信息获取token
     * @return token
     */
    private String getToken() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", keyCabinetName);
        jsonObject.put("Psw", keyCabinetPwd);
        JSONObject res = restTemplate.postForEntity(keyCabinetBaseUrl + KeyCabinetConstants.GET_TOKEN_URL, jsonObject, JSONObject.class).getBody();
        if (!KeyCabinetConstants.RESULT_CODE_SUCCESS.equals(Objects.requireNonNull(res).getString(KeyCabinetConstants.RESULT_CODE))) {
            throw new CommonException(ErrorCode.KEY_CABINET_OPENAPI_ERROR, String.valueOf(res.get(KeyCabinetConstants.RESULT_MESSAGE)));
        }
        return res.getJSONObject(KeyCabinetConstants.RESULT_DATA).getString(KeyCabinetConstants.ACCESS_TOKEN);
    }

    /**
     * 获取用户在指定时间范围内钥匙取还记录信息
     * @param token token
     * @param startTime 范围开始时间
     * @param userNo 用户工号
     * @return 钥匙取还记录列表
     */
    private List<KeyCabinetResDTO> getKeyCabinetRecords(String token, String startTime, String userNo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
        headers.add("Authorization", "Bearer " + token);
        String url = keyCabinetBaseUrl + KeyCabinetConstants.GET_KEY_RECORDS_URL;
        JSONObject params = new JSONObject();
        params.put("DepartmentId", keyCabinetDept);
        params.put("UserNumber", userNo);
        // 取钥匙状态 取钥匙、还钥匙
        params.put("State", "还钥匙");
        params.put("StartTime", startTime);
        params.put("EndTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        params.put("PageIndex", 1);
        params.put("PageSize", 50);
        HttpEntity<String> strEntity = new HttpEntity<>(params.toJSONString(), headers);
        JSONObject res = restTemplate.postForObject(url, strEntity, JSONObject.class);
        if (!KeyCabinetConstants.RESULT_CODE_SUCCESS.equals(Objects.requireNonNull(res).getString(KeyCabinetConstants.RESULT_CODE))) {
            throw new CommonException(ErrorCode.KEY_CABINET_OPENAPI_ERROR, String.valueOf(res.get(KeyCabinetConstants.RESULT_MESSAGE)));
        }
        return JSONArray.parseArray(res.getJSONObject(KeyCabinetConstants.RESULT_DATA).toJSONString(), KeyCabinetResDTO.class);
    }

    /**
     * 保单信息推送乘务系统
     * @param orderInfo 报单信息
     */
    private void sendOrderToOsm(OrderReqDTO orderInfo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("driverInfoId", orderInfo.getId());
        jsonObject.put("kilometers", orderInfo.getKilometer());
        jsonObject.put("time", orderInfo.getDate() + " 00:00:00");
        jsonObject.put("driverId", orderInfo.getUserId());
        UserAccountDetailResDTO user = dmUserAccountMapper.getUserDetail(orderInfo.getDriverId());
        if (!Objects.isNull(user)) {
            jsonObject.put("name", user.getUserViewName());
        } else {
            jsonObject.put("name", "");
        }
        jsonObject.put("jobNumber", orderInfo.getDriverId());
        JSONObject res = restTemplate.postForEntity(ocmBaseUrl + OcmConstants.DRIVER_REPORT_RECEIVE_URL, jsonObject, JSONObject.class).getBody();
        log.info(JSONObject.toJSONString(res));
        if (!OcmConstants.RESULT_CODE_SUCCESS.equals(Objects.requireNonNull(res).getString(OcmConstants.RESULT_CODE))) {
            throw new CommonException(ErrorCode.OCM_OPENAPI_ERROR, String.valueOf(res.get(OcmConstants.RESULT_MSG)));
        }
    }

    @Override
    public List<UserFaceFeatureResDTO> featureList() {
        return dmUserAccountMapper.featureList();
    }

    @Override
    @Transactional
    public Integer faceRegister(CurrentLoginUser currentLoginUse, List<HashMap<String, Object>> list) {
        List<String> faceList = new ArrayList<>();
        for (HashMap<String, Object> map : list) {
            faceList.add(map.get("feature") + "");
        }
        try {
            dmUserAccountMapper.addUserFace(currentLoginUse.getPersonId(), faceList);
            return dmUserAccountMapper.updateFace(currentLoginUse.getPersonId());
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INSERT_ERROR);
        }
    }

    @Override
    public String keyCabinetTest(CurrentLoginUser currentLoginUser, String offTime, Integer type, String day) {
        return getKeyCabinet(currentLoginUser, offTime, type, day);
    }

    @Override
    public Page<ScreenResDTO> screen(PageReqDTO pageReqDTO) {
        PageMethod.startPage(pageReqDTO.getPageNo(), pageReqDTO.getPageSize());
        int classType;
        if (DateUtils.dutyTimeDetermine(0, 7)) {
            classType = 1;
        } else if (DateUtils.dutyTimeDetermine(7, 15)) {
            classType = 2;
        } else {
            classType = 3;
        }
        Page<ScreenResDTO> page = dmUserAccountMapper.screen(pageReqDTO.of(), classType);
        List<ScreenResDTO> list = page.getRecords();
        if (com.zzj.utils.StringUtils.isNotEmpty(list)) {
            for (ScreenResDTO res : list) {
                res.setAttentime(DateUtils.timeChange(res.getAttentime()));
            }
        }
        page.setRecords(list);
        return page;
    }

    private SystemUserResDTO loginByPwd(UserLoginReqDTO userLoginReqDTO) {
        SystemUserResDTO user = eipUserAccountMapper.getUserByName(userLoginReqDTO.getUsername());
        if (StringUtils.isEmpty(userLoginReqDTO.getUsername()) || StringUtils.isEmpty(userLoginReqDTO.getPassword()) || Objects.isNull(user)) {
            throw new CommonException(ErrorCode.USER_OR_PWD_ERROR);
        }
        String userPwd = CrytogramUtil.encrypt(CrytogramUtil.decrypt64(userLoginReqDTO.getPassword()), "MD5");
        if (!userPwd.equals(user.getUserPassword())) {
            throw new CommonException(ErrorCode.USER_OR_PWD_ERROR);
        }
        IamUserResDTO iamUser = dmUserAccountMapper.getUserByName(userLoginReqDTO.getUsername());
        user.setIamUserId(iamUser.getId());
        return user;
    }


    private SystemUserResDTO loginByCard(UserLoginReqDTO userLoginReqDTO) {
        String uuid = userLoginReqDTO.getCardNo();
        log.info("=====登录卡号 before uuid：" + uuid);
        uuid = cardChange(uuid);
        log.info("=====登录卡号 after uuid：" + uuid);
        SysUserCardResDTO cardUser = dmUserAccountMapper.getUserByCard(uuid);
        if (Objects.isNull(cardUser)) {
            throw new CommonException(ErrorCode.CARD_USER_ERROR);
        }
        try {
            SystemUserResDTO user = eipUserAccountMapper.getUserByName(cardUser.getUserNo());
            IamUserResDTO iamUser = dmUserAccountMapper.getUserByName(cardUser.getUserNo());
            user.setIamUserId(iamUser.getId());
            return user;
        } catch (Exception e) {
            throw new CommonException(ErrorCode.CARD_ERROR);
        }
    }

    private SystemUserResDTO loginByFace(UserLoginReqDTO userLoginReqDTO) {
        SystemUserResDTO user = eipUserAccountMapper.getUserByName(userLoginReqDTO.getUsername());
        IamUserResDTO iamUser = dmUserAccountMapper.getUserByName(userLoginReqDTO.getUsername());
        user.setIamUserId(iamUser.getId());
        return user;
    }

    public String cardChange(String cardUuid) {
        //20 101C0FBF 000820   16
        String excludeHead = (cardUuid.substring(2, cardUuid.length()));
        String atqaSakStr = (cardUuid.substring(cardUuid.length() - 6));
        return excludeHead.replace(atqaSakStr, "");
    }
}
