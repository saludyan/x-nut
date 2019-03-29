package nut.thas.defind.mq;

import cn.hutool.json.JSONUtil;

/**
 * Created by Super Yan on 2018/8/31.
 */
public class XSerializer {


    public static byte[] serialize(XData data){
        return data.toJson().getBytes();
    }

    public static XData deserialize(byte[] data){
        String json = new String(data);
        XData xData = JSONUtil.toBean(json,XData.class);
        return xData;
    }


}
