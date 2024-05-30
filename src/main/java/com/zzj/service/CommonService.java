package com.zzj.service;

import com.zzj.dto.JXResDTO;
import com.zzj.dto.KQResDTO;
import com.zzj.dto.req.UserLoginReqDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zx
 */
public interface CommonService {


    List<KQResDTO> queryKq(String date);
    JXResDTO queryJx();


}
