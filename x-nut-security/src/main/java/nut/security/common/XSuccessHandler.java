package nut.security.common;

import cn.hutool.json.JSONUtil;
import nut.security.common.jwt.JwtTokenUtil;
import nut.security.common.jwt.XJwtUser;
import nut.thas.defind.rest.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class XSuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(new XJwtUser().setUsername(userDetails.getUsername()));
        XResultWrapper.wrap(response,true,token);
    }
}
