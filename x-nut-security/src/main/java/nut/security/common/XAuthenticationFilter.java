package nut.security.common;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j;
import nut.security.exceptions.XSecurityException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Log4j
public class XAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    private boolean postOnly = true;


    public XAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        XSuperUser xSuperUser = this.getXSuperUser(request);
        Assert.notBlank(xSuperUser.getUsername(), "Username parameter must not be null");
        Assert.notBlank(xSuperUser.getPassword(), "Password parameter must not be null");

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                xSuperUser.getUsername(), xSuperUser.getPassword());

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }


    protected void setDetails(HttpServletRequest request,
                              UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    private XSuperUser getXSuperUser(HttpServletRequest request) {
        XSuperUser xSuperUser = null;
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)
                || request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            try (InputStream is = request.getInputStream()) {
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                xSuperUser = JSONUtil.toBean(responseStrBuilder.toString(), XSuperUser.class);
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e);
                throw new XSecurityException("无法获取参数");
            }
        }
        return xSuperUser;
    }


}