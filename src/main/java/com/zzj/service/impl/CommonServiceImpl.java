package com.zzj.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzj.dto.JXMonthResDTO;
import com.zzj.dto.JXResDTO;
import com.zzj.dto.JXYearResDTO;
import com.zzj.dto.KQResDTO;
import com.zzj.service.CommonService;
import com.zzj.utils.HttpUtils;
import com.zzj.utils.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * description:
 *
 * @author zhangxin
 * @version 1.0
 * @date 2024/4/8 19:56
 */
@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    @Resource(name = "asyncAPIExecutor")
    protected ThreadPoolTaskExecutor apiExecutor;

    @Value("${open-opi.kq.nocm}")
    private String kqNocm;

    @Value("${open-opi.kq.nodm}")
    private String kqNodm;
    @Value("${open-opi.kq.nosm}")
    private String kqNosm;

    @Value("${open-opi.jx.nocm}")
    private String jxNocm;

    @Value("${open-opi.jx.nodm}")
    private String jxNodm;

    @Value("${open-opi.jx.nosm}")
    private String jxNosm;

    @Override
    @SneakyThrows
    public List<KQResDTO> queryKq(String date) {
        List<KQResDTO> kqList = new ArrayList<>();
        CompletableFuture<List<KQResDTO>> task1 = CompletableFuture.supplyAsync(() -> callKq(kqNocm,date),
                apiExecutor);
        CompletableFuture<List<KQResDTO>> task2 = CompletableFuture.supplyAsync(() -> callKq(kqNodm,date),
                apiExecutor);
        CompletableFuture<List<KQResDTO>> task3 = CompletableFuture.supplyAsync(() -> callKq(kqNosm,date),
                apiExecutor);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(task1, task2);
        allOf.get();
        kqList.addAll(task1.get());
        kqList.addAll(task2.get());
        kqList.addAll(task3.get());
        return kqList;
    }

    @Override
    @SneakyThrows
    public JXResDTO queryJx() {
        JXResDTO jxRes = new JXResDTO();

        //#乘务nocm、行车nodm、站务nosm、票务notm
        CompletableFuture<JXResDTO> task1 = CompletableFuture.supplyAsync(() -> callJx(jxNocm,"NOCM","乘务管理系统"),
                apiExecutor);
        CompletableFuture<JXResDTO> task2 = CompletableFuture.supplyAsync(() -> callJx(jxNodm,"NODM","行车调度系统"),
                apiExecutor);
        CompletableFuture<JXResDTO> task3 = CompletableFuture.supplyAsync(() -> callJx(jxNosm,"NOSM","站务管理系统"),
                apiExecutor);
        CompletableFuture<Void> allOf = CompletableFuture.allOf(task1, task2,task3);
        allOf.get();

        List<JXMonthResDTO> jxMonth =new ArrayList<>();
        List<JXYearResDTO> jxYear =new ArrayList<>();


        if(task1.get() != null && task1.get().getMonthVoList() != null){
            jxMonth.addAll(task1.get().getMonthVoList());
        }
        if(task2.get() != null && task2.get().getMonthVoList() != null){
            jxMonth.addAll(task2.get().getMonthVoList());

        }
        if(task3.get() != null && task3.get().getMonthVoList() != null){
            jxMonth.addAll(task3.get().getMonthVoList());
        }

        if(task1.get() != null && task1.get().getYearVoList() != null){
            jxYear.addAll(task1.get().getYearVoList());
        }
        if(task2.get() != null && task2.get().getYearVoList() != null){
            jxYear.addAll(task2.get().getYearVoList());
        }
        if(task3.get() != null && task3.get().getYearVoList() != null){
            jxYear.addAll(task3.get().getYearVoList());
        }

        jxRes.setMonthVoList(jxMonth);
        jxRes.setYearVoList(jxYear);
        return jxRes;
    }

    private List<KQResDTO> callKq(String url,String date){
        if(StringUtils.isNotEmpty(date)){
            url=url+"?date="+date;
        }
        JSONObject res = JSONObject.parseObject(HttpUtils.doGet(url, null), JSONObject.class);
        String data = res.getString("data");
        if(StringUtils.isNotEmpty(data)){
            return JSONArray.parseArray(data, KQResDTO.class);
        }
        return null;
    }

    private JXResDTO callJx(String url,String sysCode,String sysName){
        JSONObject res = JSONObject.parseObject(HttpUtils.doGet(url, null), JSONObject.class);
        if(StringUtils.isNotEmpty(res.getString("data")) && StringUtils.isNotEmpty(res.getString("data").replace("[","").replace("]",""))){
            JSONObject data = JSONObject.parseObject(res.getString("data").toString(), JSONObject.class);
            if(data != null){
                String monthVoList = data.getString("monthVoList");
                String yearVoList = data.getString("yearVoList");
                List<JXMonthResDTO> monthList = new ArrayList<>();
                List<JXYearResDTO> yearList = new ArrayList<>();
                if(StringUtils.isNotEmpty(monthVoList)){
                    monthList = JSONArray.parseArray(monthVoList, JXMonthResDTO.class);
                    for(JXMonthResDTO jxMonthResDTO:monthList){
                        jxMonthResDTO.setSysCode(sysCode);
                        jxMonthResDTO.setSystemName(sysName);
                    }
                }
                if(StringUtils.isNotEmpty(yearVoList)){
                    yearList = JSONArray.parseArray(yearVoList, JXYearResDTO.class);
                    for(JXYearResDTO jxMonthResDTO:yearList){
                        jxMonthResDTO.setSysCode(sysCode);
                        jxMonthResDTO.setSystemName(sysName);
                    }
                }

                JXResDTO jxRes = new JXResDTO();
                jxRes.setMonthVoList(monthList);
                jxRes.setYearVoList(yearList);
                return jxRes;
            }
            return null;
        }

        return null;
    }
}
