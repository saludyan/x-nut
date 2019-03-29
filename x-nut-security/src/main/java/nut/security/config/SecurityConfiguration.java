package nut.security.config;


import lombok.extern.slf4j.Slf4j;
import nut.jpa.component.AppCtxProvider;
import nut.security.XUserDetailsInterface;
import nut.security.common.*;
import nut.security.common.jwt.JwtAuthorizationTokenFilter;
import nut.security.common.jwt.JwtTokenUtil;
import nut.security.exceptions.XSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Yan
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Autowired
    private XSuccessHandler xSuccessHandler;

    @Autowired
    private XFailureHandler xFailureHandler;

    @Autowired
    private XPasswordEncoder xPasswordEncoder;

    @Autowired
    private XAccessDeniedHandler xAccessDeniedHandler;

    @Value("${jwt.header:x-auth}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(getUserDetailsService()).passwordEncoder(xPasswordEncoder);
    }

    private UserDetailsService getUserDetailsService(){
        if(userDetailsService == null){
            userDetailsService = AppCtxProvider.getBean(XUserDetailsInterface.class);
            if(userDetailsService == null){
                throw new XSecurityException("请实现「 XUserDetailsInterface 」接口! ヽ(`З’)ﾉ ");
            }
        }
        return userDetailsService;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/**").permitAll()
                .and()
                .authorizeRequests()    // 定义哪些URL需要被保护、哪些不需要被保护
                .antMatchers("/auth/login").permitAll()   // 设置所有人都可以访问的路径
                .antMatchers("/register").permitAll()   // 设置所有人都可以访问的路径
                .antMatchers("/register/**").permitAll()   // 设置所有人都可以访问的路径
                .anyRequest()        // 任何请求,登录后可以访问
                .authenticated()
                .and()
                .csrf().disable();     // 关闭csrf防护

        // 禁用缓存
        http.headers().cacheControl();

        // 基于token,不需要session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        JwtAuthorizationTokenFilter jwtAuthorizationTokenFilter = new JwtAuthorizationTokenFilter(getUserDetailsService(),jwtTokenUtil,tokenHeader);

        http.addFilterBefore(jwtAuthorizationTokenFilter,UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(xAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().accessDeniedHandler(xAccessDeniedHandler);
    }

    public XAuthenticationFilter xAuthenticationFilter() throws Exception {
        XAuthenticationFilter filter = new XAuthenticationFilter();
        filter.setAuthenticationSuccessHandler(xSuccessHandler);
        filter.setAuthenticationFailureHandler(xFailureHandler);
        filter.setFilterProcessesUrl("/auth");
        //这句很关键，重用WebSecurityConfigurerAdapter配置的AuthenticationManager，不然要自己组装AuthenticationManager
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }


}
