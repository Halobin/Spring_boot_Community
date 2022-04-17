package com.halobin.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //Md5
    public static String md5(String msg){
        if(StringUtils.isBlank(msg)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(msg.getBytes());
    }

    //转Json
    public static String getJSONString(int code, String Msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", Msg);
        if(map != null){
            for(String key : map.keySet()){
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String Msg){
        return getJSONString(code, Msg, null);
    }
    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }
}
