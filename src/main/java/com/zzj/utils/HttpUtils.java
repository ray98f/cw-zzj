package com.zzj.utils;


import com.zzj.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * http工具类
 * @author  Ray
 * @version 1.0
 * @date 2024/03/06
 */
@Slf4j
public class HttpUtils {

    /**
     * 以post方式调用对方接口方法
     *
     * @param pathUrl
     */
    public static String doPost(String pathUrl, String data) {
        return doPost(pathUrl, data, null);
    }

    public static String doPost(String pathUrl, String data, String authorization) {
        OutputStreamWriter out = null;
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        data = data == null ? "{}" : data;
        try {
            HttpURLConnection conn = getHttpURLConnection(pathUrl, "POST");

            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            // 维持长链接
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            if (!StringUtils.isEmpty(authorization)) {
                conn.setRequestProperty("Authorization", authorization);
            }

            //连接，从上述url.openConnection()至此的配置必须要在connect之前完成，
            conn.connect();

            //获取URLConnection对象对应的输出流
            //此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，所以在开发中不调用上述的connect()也可以)。
            out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            //发送请求参数即数据
            out.write(data);
            //flush输出流的缓冲
            out.flush();

            //获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            //构造一个字符流缓存
            br = new BufferedReader(new InputStreamReader(is));
            String str = "";
            while ((str = br.readLine()) != null) {
                result.append(str);
            }
            //关闭流
            is.close();
            //断开连接，disconnect是在底层tcp socket链接空闲时才切断，如果正在被其他线程使用就不切断。
            conn.disconnect();
        } catch (Exception e) {
            log.error("exception message", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                log.error("exception message", e);
            }
        }
        //处理流文件
        return result.toString();
    }

    private static HttpURLConnection getHttpURLConnection(String pathUrl, String POST) throws IOException {
        URL url = new URL(pathUrl);

        //打开和url之间的连接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //设定请求的方法为"POST"，默认是GET
        //post与get的不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内。
        conn.setRequestMethod(POST);

        //设置30秒连接超时
        conn.setConnectTimeout(30000);
        //设置30秒读取超时
        conn.setReadTimeout(30000);

        // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;
        conn.setDoOutput(true);
        // 设置是否从httpUrlConnection读入，默认情况下是true;
        conn.setDoInput(true);

        // Post请求不能使用缓存
        conn.setUseCaches(false);
        return conn;
    }

    /**
     * 以get方式调用对方接口方法
     * @param pathUrl url
     */
    public static void doGet(String pathUrl) {
        doGet(pathUrl, null);
    }

    public static String doGet(String pathUrl, String authorization) {
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        try {
            HttpURLConnection conn = getHttpURLConnection(pathUrl, "GET");

            //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            //维持长链接
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            if (!StringUtils.isEmpty(authorization)) {
                conn.setRequestProperty("Authorization", authorization);
            }

            //连接，从上述url.openConnection()至此的配置必须要在connect之前完成，
            conn.connect();

            //获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            //构造一个字符流缓存
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String str = "";
            while ((str = br.readLine()) != null) {
                result.append(str);
            }
            //关闭流
            is.close();
            //断开连接，disconnect是在底层tcp socket链接空闲时才切断，如果正在被其他线程使用就不切断。
            conn.disconnect();
        } catch (Exception e) {
            log.error("exception message", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                log.error("exception message", e);
            }
        }
        return result.toString();
    }

    public static String doGet(String pathUrl, String authorization,String appKey) {
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        try {
            HttpURLConnection conn = getHttpURLConnection(pathUrl, "GET");

            //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            //维持长链接
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            if (!StringUtils.isEmpty(authorization)) {
                conn.setRequestProperty("Authorization", authorization);
            }

            if (!StringUtils.isEmpty(appKey)) {
                conn.setRequestProperty("App-Key", appKey);
            }

            //连接，从上述url.openConnection()至此的配置必须要在connect之前完成，
            conn.connect();

            //获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            //构造一个字符流缓存
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String str = "";
            while ((str = br.readLine()) != null) {
                result.append(str);
            }
            //关闭流
            is.close();
            //断开连接，disconnect是在底层tcp socket链接空闲时才切断，如果正在被其他线程使用就不切断。
            conn.disconnect();
        } catch (Exception e) {
            log.error("exception message", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                log.error("exception message", e);
            }
        }
        return result.toString();
    }

    public static String getRealRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || CommonConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || CommonConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || CommonConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || CommonConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ip) || CommonConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (null != ip && ip.contains(CommonConstants.COMMA)) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }

}