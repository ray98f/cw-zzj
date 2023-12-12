package com.zzj.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzj.constant.CommonConstants;
import com.zzj.dto.req.*;
import com.zzj.dto.res.*;
import com.zzj.entity.SysUser;
import com.zzj.entity.SysUserAccount;
import com.zzj.enums.ErrorCode;
import com.zzj.exception.CommonException;
import com.zzj.mapper.UserAccountMapper;
import com.zzj.mapperDM.DmUserAccountMapper;
import com.zzj.mapperEip.EipUserAccountMapper;
import com.zzj.service.UserAccountService;
import com.zzj.shiro.CurrentLoginUser;
import com.zzj.utils.CrytogramUtil;
import com.zzj.utils.FileUtil;
import com.zzj.utils.SnowFlakeIdUtils;
import com.zzj.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author frp
 */
@Service
@Slf4j
@Transactional
public class UserAccountServiceImpl implements UserAccountService {

    @Value("${keyBox.auth-url}")
    private String keyBoxAuthUrl;

    @Value("${keyBox.user-name}")
    private String keyBoxUserName;

    @Value("${keyBox.user-pwd}")
    private String keyBoxUserPwd;

    @Value("${keyBox.record-url}")
    private String keyBoxRecordUrl;

    @Value("${keyBox.record-url}")
    private String keyBoxUserDept;

    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private EipUserAccountMapper eipUserAccountMapper;

    @Autowired
    private DmUserAccountMapper dmUserAccountMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Resource(name = "zzj2SqlSessionFactory")
    private SqlSessionFactory zzj2SqlSessionFactory;

    private String MDM_USER_CARD_URL = "http://esb.wzmtr.com:7003/mdmwebservice/ps/getAllCardList?wsdl";

    @Override
    public String getUser(UserLoginReqDTO userLoginReqDTO) {
        SystemUserResDTO user = null;
        switch (userLoginReqDTO.getLoginType()){
            case CommonConstants.LOGIN_TYPE_NORMAL:
                user = loginByPwd(userLoginReqDTO);
                break;
            case CommonConstants.LOGIN_TYPE_CARD:
                user = loginByCard(userLoginReqDTO);
                break;
            case CommonConstants.LOGIN_TYPE_FACE:
                user = loginByFace(userLoginReqDTO);
                break;
            default:
                break;
        }
        if (Objects.isNull(user)) {
            throw new CommonException(ErrorCode.USER_OR_PWD_ERROR);
        }
        CurrentLoginUser currentLoginUser = new CurrentLoginUser(user.getUserId(),user.getUserName(),user.getUserViewName(),user.getIamUserId());
        return TokenUtil.createSimpleToken(currentLoginUser);
    }

    @Override
    public UserAccountDetailResDTO userDetail(CurrentLoginUser currentLoginUser) {
        //
        UserAccountDetailResDTO user = dmUserAccountMapper.getUserDetail(currentLoginUser.getPersonId());
        if (Objects.isNull(user)) {
            throw new CommonException(ErrorCode.RESOURCE_NOT_EXIST);
        }

        List<UserPositionResDTO> positionList = dmUserAccountMapper.getUserPosition(currentLoginUser.getUserId());
        StringBuilder positionStr = new StringBuilder(",");
        for (UserPositionResDTO position : positionList){
            positionStr.append(position.getPositionName()).append(",");
        }
        user.setPositionNameStr(positionStr.toString());
        user.setPositionList(positionList);

        // 查询当日三交三问已通过的记录
        Integer recordCount = dmUserAccountMapper.getPassRecordCount(currentLoginUser.getUserId());
        if (recordCount > 0) {
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
        if(cnfList.size() == 0){
            list = dmUserAccountMapper.getExamList(CommonConstants.DEFAULT_EXAM_COUNT + 1 );
        } else {
            for(ExamConfigDetailResDTO cnf : cnfList){
                list.addAll(dmUserAccountMapper.getExamByCnf(cnf.getExamtypeId(),cnf.getExamnum()+1));
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamRecordReqDTO saveExam(CurrentLoginUser currentLoginUser,Map<String, List<String>> paraMap) {
        Object[] examList = paraMap.get("examList").toArray();
        Object[] examAnswer = paraMap.get("examAnswer").toArray();
        Object[] examCorrect = paraMap.get("examCorrect").toArray();
        int errCount = 0;
        List<RecordDetailReqDTO> recordDetailList = new ArrayList<>();
        for(int i = 0; i<examList.length; i++){
            RecordDetailReqDTO recordDetail = new RecordDetailReqDTO();
            recordDetail.setRelExamId(Integer.parseInt(examList[i].toString()));
            recordDetail.setRelAnswer(examAnswer[i].toString());
            if((examAnswer[i]+"").equals(examCorrect[i]+"")){
                recordDetail.setRelCorrect(1);
            }else{
                errCount += 1;
                recordDetail.setRelCorrect(0);
            }
            recordDetailList.add(recordDetail);
        }
        ExamRecordReqDTO er = new ExamRecordReqDTO();
        er.setUserId(currentLoginUser.getUserId());
        er.setReCount(examList.length);
        er.setReCountError(errCount);
        er.setRePercent((double)(Math.round((examList.length - errCount)*100/examList.length)/100.0));

        try{
            Integer rec = dmUserAccountMapper.addRecord(er);
            if(rec > 0){
                dmUserAccountMapper.addRecordDetail(recordDetailList,er.getReId());
            }
        }catch (Exception e){
            throw new CommonException(ErrorCode.INSERT_ERROR);
        }
        return er;
    }

    @Override
    public DutyDetailResDTO getDutyInfo(CurrentLoginUser currentLoginUser) {

        DutyDetailResDTO res = dmUserAccountMapper.getDutyInfo(currentLoginUser.getUserId());
        if(res == null || CommonConstants.OFF_CR_NAME.equals(res.getCrName())){
            //未排班
            throw new CommonException(ErrorCode.RESOURCE_NOT_EXIST);
        }
        res.setAttentime(timeChange(res.getAttentime()));
        res.setOfftime(timeChange(res.getOfftime()));
        List<DmAttendQuitResDTO> workList = dmUserAccountMapper.getAttendQuit(res.getId());
        if(workList.size() == 0){
            res.setAttendFlag(0);
            res.setQuitFlag(0);
        }else if(workList.size() == 1){
            res.setAttendFlag(1);
            res.setQuitFlag(0);
        }else {
            res.setAttendFlag(1);
            res.setQuitFlag(1);
        }
        res.setWorkRecord(workList);

        return res;
    }

    @Override
    public DutyDetailResDTO getNextDutyInfo(CurrentLoginUser currentLoginUser, String recDate) {
        try{
            DutyDetailResDTO res = dmUserAccountMapper.getNextDutyInfo(currentLoginUser.getPersonId(),recDate);
            if(res != null){
                res.setAttentime(timeChange(res.getAttentime()));
                res.setOfftime(timeChange(res.getOfftime()));
            }
            return res;
        }catch (Exception e) {
            throw new CommonException(ErrorCode.RESOURCE_NOT_EXIST);
        }
    }

    @Override
    public String dutyOn(CurrentLoginUser currentLoginUser,AttendQuitReqDTO attendQuitReqDTO) {

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:m:s");
        String attendTime = sdf.format(new Date());
        attendQuitReqDTO.setId(snowflakeGenerator.next());
        attendQuitReqDTO.setWorkType("1");
        attendQuitReqDTO.setUserId(currentLoginUser.getUserId());
        attendQuitReqDTO.setActionTime(attendTime);
        Integer res = dmUserAccountMapper.dutyOn(attendQuitReqDTO);
        if(res > 0){
            return attendTime;
        }
        return "";
    }

    @Override
    public String dutyOff(CurrentLoginUser currentLoginUser,AttendQuitReqDTO attendQuitReqDTO) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:m:s");
        String quitTime = sdf.format(new Date());
        attendQuitReqDTO.setId(snowflakeGenerator.next());
        attendQuitReqDTO.setWorkType("2");
        attendQuitReqDTO.setActionTime(quitTime);
        attendQuitReqDTO.setUserId(currentLoginUser.getUserId());
        Integer res = dmUserAccountMapper.dutyOff(attendQuitReqDTO);
        if(res > 0){
            return quitTime;
        }
        return "";
    }

    @Override
    public List<TrainScheduleDTO> orderInit(CurrentLoginUser currentLoginUser, String stringRunList) {
        if(StringUtils.isEmpty(stringRunList)){
            return null;
        }
        List<String> trainIds = Arrays.asList(stringRunList.split(CommonConstants.TRAIN_SPLIT));
        List<TrainScheduleDTO> res = dmUserAccountMapper.getTrainSchedules(trainIds);
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveOrder(OrderReqDTO orderInfo) {

        try{
            orderInfo.setId(snowflakeGenerator.next());
            Integer res = dmUserAccountMapper.saveOrderInfo(orderInfo);
            if(res > 0){
                dmUserAccountMapper.addOrderDetail(orderInfo.getList(),orderInfo.getId());
            }
        }catch (Exception e){
            throw new CommonException(ErrorCode.INSERT_ERROR);
        }
        return orderInfo.getId()+"";
    }

    @Override
    public List<TrainStationResDTO> getStationList(String trainNum) {
        return dmUserAccountMapper.getTrainStation(trainNum);
    }

    @Override
    public List<UserFaceFeatureResDTO> featureList() {
        return dmUserAccountMapper.featureList();
    }

    @Override
    @Transactional
    public Integer faceRegister(CurrentLoginUser currentLoginUse,List<HashMap<String, Object>> list) {
        List<String> faceList = new ArrayList<>();
        for(HashMap<String, Object> map: list){
            faceList.add(map.get("feature")+"");
        }
        try{
            dmUserAccountMapper.addUserFace(currentLoginUse.getPersonId(),faceList);
            Integer res = dmUserAccountMapper.updateFace(currentLoginUse.getPersonId());
            return res;
        }catch (Exception e){
            throw new CommonException(ErrorCode.INSERT_ERROR);
        }
    }

    @Override
    public CheckKeyStoreResDTO checkKeyStore(CurrentLoginUser currentLoginUser, HashMap<String, Object> map) {
        CheckKeyStoreResDTO res = new CheckKeyStoreResDTO();

        Long id = Long.parseLong(map.get("id").toString());

        CheckDutyResDTO dutyInfo = dmUserAccountMapper.checkDutyInfo(currentLoginUser.getUserId(),id);
        res.setCheckDutyInfo(dutyInfo);
        if(dutyInfo != null && StrUtil.contains(dutyInfo.getCrName(),CommonConstants.DUTY_OFF_CR_NAME_CHECK)){
            List<UserKeyStoreRecordResDTO> list = dmUserAccountMapper.getKeyRecord(dutyInfo);
            if(list == null || list.size() == 0){

                //TODO 调用钥匙柜接口查询本日归还记录
                String accessToken = getKeyBoxAuth();
                List<RecordData> recList = getKeyBoxRecord(dutyInfo,accessToken);
                if(recList != null && recList.size() > 0){
                    res.setCheckRes(1); //晚班  有归还记录
                    RecordData rec = recList.get(0);

                    //柜子编号,钥匙编号 格子
                    String boxNum = rec.getBoxNumber().toString();
                    String keyNum = String.format("%03d", rec.getKeyNumber());
                    KeyCabinetResDTO keyInfo = dmUserAccountMapper.getKeyCabinetInfo(boxNum,keyNum);

                    UserKeyStoreRecordResDTO userKeyStoreRecord = new UserKeyStoreRecordResDTO();
                    SnowFlakeIdUtils snowFlakeIdUtils = new SnowFlakeIdUtils();
                    userKeyStoreRecord.setId(snowFlakeIdUtils.nextId());
                    userKeyStoreRecord.setCabinetId(keyInfo.getBoxId());
                    userKeyStoreRecord.setLatticeId(keyInfo.getKeyId());
                    userKeyStoreRecord.setUserId(dutyInfo.getNewDriverInfoId());
                    userKeyStoreRecord.setUseTime(new Date());
                    userKeyStoreRecord.setSchedulingId(dutyInfo.getId());
                    userKeyStoreRecord.setCrossingId(dutyInfo.getCrId());
                    dmUserAccountMapper.insertKeyRecord(userKeyStoreRecord);

                    res.setUserKeyStoreRecordRes(userKeyStoreRecord);

                }else {
                    res.setCheckRes(0); //晚班 未归还钥匙
                }

                return res;
            }else{
                res.setCheckRes(1);//晚班 有存放记录 可归还
                res.setUserKeyStoreRecordRes(list.get(0)); //记录信息
                return res;
            }
        }else{
            res.setCheckRes(3);//非晚班不检测钥匙存放
            return res;
        }

    }

    private SystemUserResDTO loginByPwd(UserLoginReqDTO userLoginReqDTO){
        SystemUserResDTO user = eipUserAccountMapper.getUserByName(userLoginReqDTO.getUsername());
        if (StringUtils.isEmpty(userLoginReqDTO.getUsername()) || StringUtils.isEmpty(userLoginReqDTO.getPassword())
                || Objects.isNull(user)) {
            return null;
        }
        String userPwd = CrytogramUtil.encrypt(CrytogramUtil.decrypt64(userLoginReqDTO.getPassword()).toString(),"MD5")+"";
        if(!userPwd.equals(user.getUserPassword())){
            return null;
        }
        IamUserResDTO iamUser = dmUserAccountMapper.getUserByName(userLoginReqDTO.getUsername());
        user.setIamUserId(iamUser.getId());
        return user;
    }


    private SystemUserResDTO loginByCard(UserLoginReqDTO userLoginReqDTO){
        //TODO 根据物理卡号 获取员工信息  从数据共享平台申请调用 员工卡信息接口  目前还没有 20231124
        //query api userNo
        String uuid = userLoginReqDTO.getCardNo();
        uuid = cardChange(uuid);
        try{
            SysUserCardResDTO user1 = dmUserAccountMapper.getUserByCard(uuid);
            SystemUserResDTO user = eipUserAccountMapper.getUserByName(user1.getUserNo());
            IamUserResDTO iamUser = dmUserAccountMapper.getUserByName(user1.getUserNo());
            user.setIamUserId(iamUser.getId());
            return user;
        }catch (Exception e){
            throw new CommonException(ErrorCode.RESOURCE_NOT_EXIST);
        }

    }

    private SystemUserResDTO loginByFace(UserLoginReqDTO userLoginReqDTO){
        try{
            SystemUserResDTO user = eipUserAccountMapper.getUserByName(userLoginReqDTO.getUsername());
            IamUserResDTO iamUser = dmUserAccountMapper.getUserByName(userLoginReqDTO.getUsername());
            user.setIamUserId(iamUser.getId());
            return user;
        }catch (Exception e){
            throw new CommonException(ErrorCode.RESOURCE_NOT_EXIST);
        }
    }

    private String timeChange(String timeNum){
        String timeStr = String.format("%06d",Integer.parseInt(timeNum));
        String newTimeStr = "";
        if(timeStr.length() == 6){
            newTimeStr += timeStr.substring(0,2) + ":";
            newTimeStr += timeStr.substring(2,4) + ":";
            newTimeStr += timeStr.substring(4,6);
        }
        return newTimeStr;
    }

    private String getKeyBoxAuth(){
        String accessToken = "";
        String url = keyBoxAuthUrl + "?name=" + keyBoxUserName+"&Psw=" + keyBoxUserPwd;
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(url)
                .build()
                .expand()
                .encode();
        URI uri = uriComponents.toUri();
        JSONObject res = restTemplate.getForEntity(uri, JSONObject.class).getBody();

        if(CommonConstants.KEY_BOX_RES_CODE.equals(Objects.requireNonNull(res).getInteger(CommonConstants.KEY_BOX_RESULT_CODE))){
            JSONObject data = res.getJSONObject(CommonConstants.KEY_BOX_RESULT_DATA);
            accessToken = data.getString(CommonConstants.KEY_BOX_TOKEN);
        }
        return accessToken;
    }

    private List<RecordData> getKeyBoxRecord(CheckDutyResDTO dutyInfo,String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
        headers.add("Authorization","Bearer " + accessToken);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date currentTime = new Date();
        String startTime = sdf.format(currentTime) + CommonConstants.DATE_START;
        String endTime = sdf.format(currentTime) + CommonConstants.DATE_END;

        //根据同班次两司机来查询钥匙存放记录
        InRecord inRecord = new InRecord();
        inRecord.setDepartmentId(keyBoxUserDept);//部门必填
        inRecord.setUserNumber(dutyInfo.getNewDriverNo()); // 查询 主控司机
        inRecord.setStartTime(startTime);
        inRecord.setEndTime(endTime);
        inRecord.setPageIndex(CommonConstants.DEFAULT_PAGE_INDEX);
        inRecord.setPageSize(CommonConstants.DEFAULT_PAGE_SIZE);

        HttpEntity<String> strEntity = new HttpEntity<>(JSONObject.toJSONString(inRecord), headers);
        JSONObject json = restTemplate.postForEntity(keyBoxRecordUrl, strEntity, JSONObject.class).getBody();

        if(!CommonConstants.KEY_BOX_RES_CODE.equals(Objects.requireNonNull(json).getString(CommonConstants.KEY_BOX_RESULT_CODE))){
            return null;
        }

        if (json.getJSONArray(CommonConstants.KEY_BOX_RECORD_DATA) != null) {
            return JSONArray.parseArray(json.getJSONArray(CommonConstants.KEY_BOX_RECORD_DATA).toJSONString(), RecordData.class);
        }else{
            inRecord.setUserNumber(dutyInfo.getAssistantDriverNo()); //  查询 副控司机
            HttpEntity<String> strEntity2 = new HttpEntity<>(JSONObject.toJSONString(inRecord), headers);
            JSONObject json2 = restTemplate.postForEntity(keyBoxRecordUrl, strEntity2, JSONObject.class).getBody();
            if(!CommonConstants.KEY_BOX_RES_CODE.equals(Objects.requireNonNull(json2).getString(CommonConstants.KEY_BOX_RESULT_CODE))){
                return null;
            }else{
                return JSONArray.parseArray(json2.getJSONArray(CommonConstants.KEY_BOX_RECORD_DATA).toJSONString(), RecordData.class);
            }
        }
    }

    public String cardChange(String cardUuid){
        //20 101C0FBF 000820   16
        String excludeHead = (cardUuid.substring(2,cardUuid.length()));
        String atqaSakStr = (cardUuid.substring(cardUuid.length()-6));
        return excludeHead.replace(atqaSakStr,"");
    }

}
