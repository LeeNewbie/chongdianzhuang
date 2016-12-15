package com.niuniu.superadapter.operator.hlht.calls;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niuniu.superadapter.operator.hlht.HlhtConfig;
import com.niuniu.superadapter.operator.hlht.params.HlhtNormalRequest;
import org.gof.rest.utils.AESUtil;
import org.gof.rest.utils.HMacMD5;
import org.gof.rest.utils.HttpClientUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex.Chen on 2016/12/15.
 */
public class HlhtTokenCall {


    public static String getToken() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String data = "{\"OperatorID\":\""+ HlhtConfig.OperatorID +"\"," +
                "\"OperatorSecret\":\""+HlhtConfig.OperatorSecret+"\"}";
        String timeStemp = sdf.format(new Date());
        String enString = AESUtil.Encrypt(data, HlhtConfig.SECURITY_KEY,HlhtConfig.AES_SECURITY);
        String sig = HMacMD5.getHmacMd5Str(HlhtConfig.SECURITY_KEY,HlhtConfig.OperatorID+enString+timeStemp+timeStemp);
        HlhtNormalRequest hlhtNormalRequest = new HlhtNormalRequest();
        hlhtNormalRequest.setOperatorID(HlhtConfig.OperatorID);
        hlhtNormalRequest.setData(enString);
        hlhtNormalRequest.setSig(sig);
        hlhtNormalRequest.setTimeStamp(timeStemp);
        hlhtNormalRequest.setSeq(timeStemp);
        String body = objectMapper.writeValueAsString(hlhtNormalRequest);
        String response = HttpClientUtil.doPostJson(HlhtConfig.TOKEN_URL,body);
        Map map = objectMapper.readValue(response, HashMap.class);
        String DeString = AESUtil.Decrypt((String)map.get("Data"),HlhtConfig.SECURITY_KEY);
        Map map2 = objectMapper.readValue(DeString, HashMap.class);
        String token = (String)map2.get("AccessToken");
        return token;
    }



    public static void main(String[] strs) throws Exception {
        String token = getToken();
        System.out.println(token);
    }
}
