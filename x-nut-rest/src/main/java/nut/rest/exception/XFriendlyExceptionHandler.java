package nut.rest.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONUtil;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import nut.rest.exception.custom.*;
import nut.thas.defind.rest.RestResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class XFriendlyExceptionHandler {


    private List<IException> filter;


    public XFriendlyExceptionHandler(){
        filter = new ArrayList<>();
        filter.add(new CustomMySqlException());
        filter.add(new CustomHttpRequestMethodNotSupportedException());
        filter.add(new CustomIllegalArgumentException());
        filter.add(new CustomBadCredentialsException());
    }


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public RestResponse jsonErrorHandler(HttpServletRequest req, Exception e) throws Exception {

        log.error("\n\n-----------START----------");
        log.error("\n\n\tException:\n\t{\n\t\t\t\"method\":\"{}\",\n\t\t\t\"url\":\"{}\",\n\t\t\t\"message\":\"{}\"\n\t}\n\n"
                ,req.getMethod()
                ,req.getRequestURI()
                ,e.getMessage());
        log.error("\n\t ↓↓↓↓↓↓↓↓detail↓↓↓↓↓↓↓↓ \n");
        log.error(e.getMessage(),e);
        log.error("\n\n-----------END----------");


        for (IException iException : filter) {
            RestResponse customResponse = iException.process(e);
            if(customResponse != null){
                return customResponse;
            }
        }

        log.error(e.getMessage(),e);
        return RestResponse.failed(XRestException.E4001.getCode(),e.getMessage(), ExceptionUtil.stacktraceToOneLineString(e,800));
    }

}
