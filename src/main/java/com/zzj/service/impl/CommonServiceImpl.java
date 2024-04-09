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

    @Value("${open-opi.jx.nocm}")
    private String jxNocm;

    @Value("${open-opi.jx.nodm}")
    private String jxNodm;

    @Override
    @SneakyThrows
    public List<KQResDTO> queryKq(String date) {
        List<KQResDTO> kqList = new ArrayList<>();
        CompletableFuture<List<KQResDTO>> task1 = CompletableFuture.supplyAsync(() -> callKq(kqNocm,date),
                apiExecutor);
        CompletableFuture<List<KQResDTO>> task2 = CompletableFuture.supplyAsync(() -> callKq(kqNodm,date),
                apiExecutor);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(task1, task2);
        allOf.get();
        kqList.addAll(task1.get());
        kqList.addAll(task2.get());
        return kqList;
    }

    @Override
    @SneakyThrows
    public JXResDTO queryJx() {
        JXResDTO jxRes = new JXResDTO();

        CompletableFuture<JXResDTO> task1 = CompletableFuture.supplyAsync(() -> callJx(jxNocm),
                apiExecutor);
        CompletableFuture<JXResDTO> task2 = CompletableFuture.supplyAsync(() -> callJx(jxNodm),
                apiExecutor);
        CompletableFuture<Void> allOf = CompletableFuture.allOf(task1, task2);
        allOf.get();

        List<JXMonthResDTO> jxMonth =new ArrayList<>();
        List<JXYearResDTO> jxYear =new ArrayList<>();
        if(task1.get().getMonthVoList() != null){
            jxMonth.addAll(task1.get().getMonthVoList());
        }
        if(task2.get().getMonthVoList() != null){
            jxMonth.addAll(task2.get().getMonthVoList());
        }

        if(task1.get().getYearVoList() != null){
            jxYear.addAll(task1.get().getYearVoList());
        }
        if(task2.get().getYearVoList() != null){
            jxYear.addAll(task2.get().getYearVoList());
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
        return JSONArray.parseArray(data, KQResDTO.class);
    }

    private JXResDTO callJx(String url){
        JSONObject res = JSONObject.parseObject(HttpUtils.doGet(url, null), JSONObject.class);
        JSONObject data = JSONObject.parseObject(res.getString("data").toString(), JSONObject.class);
        String monthVoList = data.getString("monthVoList");
        String yearVoList = data.getString("yearVoList");
        List<JXMonthResDTO> monthList = JSONArray.parseArray(monthVoList, JXMonthResDTO.class);
        List<JXYearResDTO> yearList = JSONArray.parseArray(yearVoList, JXYearResDTO.class);
        JXResDTO jxRes = new JXResDTO();
        jxRes.setMonthVoList(monthList);
        jxRes.setYearVoList(yearList);
        return jxRes;
    }
}
