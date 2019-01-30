package nut.rest.exception;

import nut.thas.defind.rest.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class XErrorsController {

    @Autowired
    private DefaultErrorAttributes errorAttributes;

    private static final String PATH = "/error";

    @RequestMapping(value = PATH+"/404")
    public RestResponse error404(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Map<String,Object> errorAttributes = this.getErrorAttributes(request);
        String errorMessage = (String) errorAttributes.get("path");
        String detail =String.format ("error:%s, detail:%s", errorAttributes.get("message"), errorAttributes.get("error"));
        errorMessage = String.format("[%s] 检查路径是否正确",errorMessage);
        return RestResponse.failed("404",errorMessage, detail);
    }

    @RequestMapping(value = PATH+"/500")
    public RestResponse error500(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request);
        String errorMessage = (String) body.get("path");
        String detail =String.format ("error:%s, detail:%s", body.get("message"), body.get("error"));
        errorMessage = String.format("[%s] 服务器异常,请稍后再试",errorMessage);
        return RestResponse.failed("500",errorMessage, detail);
    }



    private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return this.errorAttributes.getErrorAttributes(requestAttributes, false);
    }

}
