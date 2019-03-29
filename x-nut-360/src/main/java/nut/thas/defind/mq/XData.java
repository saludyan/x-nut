package nut.thas.defind.mq;

import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import nut.thas.exceptions.XException;

/**
 * @Author: Yan
 * @date 2019-03-27
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
public class XData {

    public XData(Object object){
        this.className = object.getClass().getName();
        this.data = new Gson().toJson(object);
    }

    private int tryCount;

    private String data;

    private String className;


    public String toJson(){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }


    public Object getOriginData(){
        try {
            if(className.contains("String")){
                return data;
            }
            Gson gson = new Gson();

            Class clazz = Class.forName(className);
            return gson.fromJson(data,clazz);
        } catch (ClassNotFoundException e) {
            throw new XException(e);
        }

    }

    public static XData toBean(String message){
        XData xData = JSONUtil.toBean(message,XData.class);
        return xData;
    }
}
