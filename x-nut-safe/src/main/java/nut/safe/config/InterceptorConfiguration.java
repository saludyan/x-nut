package nut.safe.config;

import nut.safe.component.XOauthInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan(basePackages={"org.springframework.security.*"})
public class InterceptorConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new XOauthInterceptor()).addPathPatterns("/oauth/token/**");
        registry.addInterceptor(new XOauthInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
