package com.blockchain.http;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blockchain.base.BaseData;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by: Yumira.
 * Created on: 2018/7/28-下午10:57.
 * Description: 基于RestTemplate的网络请求工具
 */
public class RequestUtil {


    public static String get(String url) {
        return get(url,new JSONObject());
    }

    public static String requestPostByJson(String url, JSONObject parmas){
        return post(url,parmas,MediaType.APPLICATION_JSON,String.class);
    }

    public static <T> T requestPostByJson(String url, JSONObject parmas,Class<T> clz){
        return post(url,parmas,MediaType.APPLICATION_JSON,clz);
    }

    public static <T> List<T> requestListByGet(String url, Class<T> clz){
        return requestListByGet(url,new JSONObject(),clz);
    }

    public static <T> List<T> requestListByGet(String url, Map parmas,Class<T> clz){
        BaseData<JSONArray> data = JsonUtil.toBean(get(url,parmas),BaseData.class);
        List<T> list = JsonUtil.toList(data.getData().toJSONString(),clz);
        return list;
    }

    public static <T> T requestByPost(String url, Class<T> clz){
        return requestByPost(url,new JSONObject(),clz);
    }

    public static <T> T requestByPost(String url,Map parmas, Class<T> clz){
        return requestByPost(url,parmas,MediaType.APPLICATION_FORM_URLENCODED,clz);
    }

    public static <T> T requestByPost(String url, Map parmas,MediaType requestType,Class<T> clz){
        BaseData<JSONObject> data = JsonUtil.toBean(post(url, JsonUtil.mapToJson(parmas),requestType,String.class),BaseData.class);
        T t = JsonUtil.toBean(data.getData().toJSONString(),clz);
        return t;
    }

    public static <T> List<T> requestListByPost(String url, Class<T> clz){
        return requestListByPost(url,new JSONObject(),clz);
    }

    public static <T> List<T> requestListByPost(String url,Map parmas, Class<T> clz){
        return requestListByPost(url,parmas,null,clz);
    }

    public static <T> List<T> requestListByPost(String url, Map parmas,MediaType requestType,Class<T> clz){
        BaseData<JSONArray> data = JsonUtil.toBean(post(url,JsonUtil.mapToJson(parmas),requestType,String.class),BaseData.class);
        List<T> list = JsonUtil.toList(data.getData().toJSONString(),clz);
        return list;
    }

    private static class DefaultResponseErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().value() != HttpServletResponse.SC_OK;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getBody()));
            StringBuilder sb = new StringBuilder();
            String str = null;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            try {
                throw new Exception(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String get(String url, Map parmas) {
        RestTemplate restTemplate = ApplicationContextProvider.getBean(RestTemplate.class);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        String response = restTemplate.getForObject(expandURL(url, parmas.keySet()), String.class, parmas);
        return response;
    }


    public static <T> T post(String url, JSONObject parmas, MediaType mediaType, Class<T> clz) {
//        JSONObject parmas = JsonUtil.mapToJson(parmaMap);
        RestTemplate restTemplate = ApplicationContextProvider.getBean(RestTemplate.class);
        //这是为 MediaType.APPLICATION_FORM_URLENCODED 格式HttpEntity 数据 添加转换器
        //还有就是，如果是APPLICATION_FORM_URLENCODED方式发送post请求，
        //也可以直接HttpHeaders requestHeaders = new HttpHeaders(createMultiValueMap(parmas)，true)，就不用增加转换器了
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        // 设置header信息
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(mediaType);

        HttpEntity<?> requestEntity = (
                mediaType == MediaType.APPLICATION_JSON
                        || mediaType == MediaType.APPLICATION_JSON_UTF8)
                ? new HttpEntity<>(parmas, requestHeaders)
                : (mediaType == MediaType.APPLICATION_FORM_URLENCODED
                ? new HttpEntity<MultiValueMap>(createMultiValueMap(parmas), requestHeaders)
                : new HttpEntity<>(null, requestHeaders));

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        T result = (mediaType == MediaType.APPLICATION_JSON || mediaType == MediaType.APPLICATION_JSON_UTF8)
                ? restTemplate.postForObject(url, requestEntity, clz)
                : restTemplate.postForObject(mediaType == MediaType.APPLICATION_FORM_URLENCODED
                ? url
                : expandURL(url, parmas.keySet()), requestEntity, clz, parmas);

        return result;
    }

    private static MultiValueMap<String, String> createMultiValueMap(Map parmasMap) {
        JSONObject parmas = JsonUtil.mapToJson(parmasMap);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for(String key : parmas.keySet()) {
            if(parmas.get(key) instanceof List) {
                for(Iterator<String> it=((List<String>) parmas.get(key)).iterator(); it.hasNext(); ) {
                    String value = it.next();
                    map.add(key, value);
                }
            } else {
                map.add(key, parmas.getString(key));
            }
        }
        return map;
    }


    private static String expandURL(String url, Set<?> keys) {
        final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
        Matcher mc = QUERY_PARAM_PATTERN.matcher(url);
        StringBuilder sb = new StringBuilder(url);
        if (mc.find()) {
            sb.append("&");
        } else {
            sb.append("?");
        }

        for (Object key : keys) {
            sb.append(key).append("=").append("{").append(key).append("}").append("&");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
