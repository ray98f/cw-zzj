package com.zzj.controller.user;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzj.annotation.CurrUser;
import com.zzj.constant.CommonConstants;
import com.zzj.dto.DataResponse;
import com.zzj.dto.req.AttendQuitReqDTO;
import com.zzj.dto.req.ExamRecordReqDTO;
import com.zzj.dto.req.OrderReqDTO;
import com.zzj.dto.res.*;
import com.zzj.service.UserAccountService;
import com.zzj.shiro.CurrentLoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zx
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "司机出退勤")
@Validated
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/info")
    @ApiOperation(value = "获取用户信息")
    public DataResponse<UserAccountDetailResDTO> user(@CurrUser CurrentLoginUser currentLoginUser) {
        return DataResponse.of(userAccountService.userDetail(currentLoginUser));
    }

    @GetMapping("/examList")
    @ApiOperation(value = "获取试题")
    public DataResponse<List<ExamResDTO>> exam(@CurrUser CurrentLoginUser currentLoginUser) {
        return DataResponse.of(userAccountService.getExamList(currentLoginUser));
    }

    @PostMapping("/examSave")
    @ApiOperation(value = "保存考试结果")
    public DataResponse<ExamRecordReqDTO> examSave(@CurrUser CurrentLoginUser currentLoginUser,
                                                   @RequestBody Map<String,List<String>> paraMap) {
        return DataResponse.of(userAccountService.saveExam(currentLoginUser,paraMap));
    }

    @GetMapping("/dutyInfo")
    @ApiOperation(value = "获取排班信息")
    public DataResponse<DutyDetailResDTO> dutyInfo(@CurrUser CurrentLoginUser currentLoginUser) {

        return DataResponse.of(userAccountService.getDutyInfo(currentLoginUser));
    }

    @PostMapping("/nextDuty")
    @ApiOperation(value = "获取下一次排班信息")
    public DataResponse<DutyDetailResDTO> nextDuty(@CurrUser CurrentLoginUser currentLoginUser) {

        return DataResponse.of(userAccountService.getNextDutyInfo(currentLoginUser));
    }

    @PostMapping("/dutyOn")
    @ApiOperation(value = "出勤")
    public DataResponse<String> attendSave(@CurrUser CurrentLoginUser currentLoginUser,@RequestBody AttendQuitReqDTO attendQuitReqDTO){
        return DataResponse.of(userAccountService.dutyOn(currentLoginUser,attendQuitReqDTO));
    }

    @PostMapping ("/checkKeyStore")
    @ApiOperation(value = "校验钥匙归还")
    @SneakyThrows
    public DataResponse<CheckKeyStoreResDTO> checkKeyStore(@CurrUser CurrentLoginUser currentLoginUser,
                                              @RequestBody HashMap<String,Object> map) {

        return DataResponse.of(userAccountService.checkKeyStore(currentLoginUser,map));
    }

    @PostMapping("/dutyOff")
    @ApiOperation(value = "退勤")
    public DataResponse<String> quitSave(@CurrUser CurrentLoginUser currentLoginUser,@RequestBody AttendQuitReqDTO attendQuitReqDTO){
        return DataResponse.of(userAccountService.dutyOff(currentLoginUser,attendQuitReqDTO));
    }

    /**
     * 根据账号所属线路获取当天车次列表
     * @param lineId 线路id
     * @return 车次列表
     */
    @GetMapping("/listTrains")
    @ApiOperation(value = "根据账号所属线路获取当天车次列表")
    public DataResponse<List<TrainsResDTO>> listTrains(@RequestParam Long lineId) {
        return DataResponse.of(userAccountService.listTrains(lineId));
    }

    /**
     * 根据账号所属线路获取车站列表
     * @param lineId 线路id
     * @return 车站列表
     */
    @GetMapping("/listStations")
    @ApiOperation(value = "根据账号所属线路获取车站列表")
    public DataResponse<List<StationsResDTO>> listStations(@RequestParam Long lineId) {
        return DataResponse.of(userAccountService.listStations(lineId));
    }

    /**
     * 根据列车和站点查询列车信息
     * @param trainId 列车id
     * @param stationId 站点id
     * @return 列车信息
     */
    @GetMapping("/getTrainSchedule")
    @ApiOperation(value = "根据列车和站点查询列车信息")
    public DataResponse<TrainScheduleDTO> getTrainSchedule(@RequestParam Long trainId, @RequestParam Long stationId) {
        return DataResponse.of(userAccountService.getTrainSchedule(trainId, stationId));
    }

    @PostMapping("/orderInit")
    @ApiOperation(value = "报单信息")
    public DataResponse<List<TrainScheduleResDTO>> orderInfo(@RequestBody Map<String,String> param) {
        return DataResponse.of(userAccountService.orderInit(param.get("stringRunList")));
    }

    @PostMapping("/saveOrder")
    @ApiOperation(value = "报单")
    public DataResponse<String> saveOrder(@RequestBody OrderReqDTO orderInfo){
        userAccountService.saveOrder(orderInfo);
        return DataResponse.success();
    }

    @GetMapping("/stationList")
    @ApiOperation(value = "获取车站")
    public DataResponse<List<TrainStationResDTO>> trainStation(@RequestParam String trainNum) {
        return DataResponse.of(userAccountService.getStationList(trainNum));
    }

    @GetMapping("/boxList")
    @ApiOperation(value = "获取钥匙柜列表")
    public DataResponse<List<T>> boxList(@RequestParam String trainNum) {
        // TODO 钥匙柜接口：终端信息查询接口（Terminal_Query）
        return null;
    }

    @GetMapping("/featureList")
    @ApiOperation(value = "获取特征列表")
    public DataResponse<List<UserFaceFeatureResDTO>> featureList(@RequestParam String tenantId) {
        if(CommonConstants.DEF_TENANT_ID.equals(tenantId)){
            return DataResponse.of(userAccountService.featureList());
        }
        return null;
    }

    @PostMapping ("/faceRegister")
    @ApiOperation(value = "获取特征列表")
    @SneakyThrows
    public DataResponse<Integer> faceRegister(@CurrUser CurrentLoginUser currentLoginUser,
                                              @RequestBody List<HashMap<String,Object>> list) {
        return DataResponse.of(userAccountService.faceRegister(currentLoginUser,list));
    }


}
