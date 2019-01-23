package nut.security.common;

import nut.thas.defind.rest.RestResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class XAuthLoginController {

    @RequestMapping("/auth/login")
    public RestResponse requireAuthentication(HttpServletRequest request, HttpServletResponse response) {

        return RestResponse.failed("permission denied");
    }
}
