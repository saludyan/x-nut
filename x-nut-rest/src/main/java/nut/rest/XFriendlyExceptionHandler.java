package nut.rest;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import nut.thas.defind.rest.RestResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class XFriendlyExceptionHandler {


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public RestResponse jsonErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        /**
         * TODO 异常友好输出(不断补充)
         */
        log.error(e.getMessage(),e);
        return RestResponse.failed("10001", ExceptionUtil.getMessage(e));
    }
}
