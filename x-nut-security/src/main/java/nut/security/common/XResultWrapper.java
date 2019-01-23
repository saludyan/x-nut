package nut.security.common;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONUtil;
import nut.security.exceptions.XSecurityException;
import nut.thas.defind.rest.RestResponse;
import nut.tool.XJsonMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class XResultWrapper {

    public static void wrap(HttpServletResponse response,boolean success,Object obj){
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();

            RestResponse restResponse = null;

            if(success){
                restResponse = RestResponse.success(obj);
            }else{
                if(obj instanceof Exception){
                    restResponse = RestResponse.failed(ExceptionUtil.getMessage((Throwable) obj));
                }else{
                    throw new XSecurityException("暂不支持类型...");
                }
            }

            // RestResponse.success(obj);
            out.write(XJsonMapper.INSTANCE.toJson(restResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            out.flush();
            out.close();
        }
    }


}
