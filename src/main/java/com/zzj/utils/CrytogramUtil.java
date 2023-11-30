package com.zzj.utils;


import org.springframework.util.Base64Utils;
import sun.misc.BASE64Encoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密方式
 *
 * @author zhangxin
 * @version 1.0
 * @date 2021/11/19 15:00
 */
public class CrytogramUtil
{

  public static String encrypt(String paramString1, String paramString2)
  {
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance(paramString2);
      localMessageDigest.reset();
      byte[] arrayOfByte1 = paramString1.getBytes();
      byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
      BASE64Encoder localBASE64Encoder = new BASE64Encoder();
      return localBASE64Encoder.encode(arrayOfByte2);
    } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
      localNoSuchAlgorithmException.printStackTrace();
    }
    return paramString1;
  }

  public static String decrypt64(String base64Str){
    byte[] xByte = Base64Utils.decodeFromString(base64Str);
    String res = new String(xByte, StandardCharsets.UTF_8);
    return res;
  }
}
