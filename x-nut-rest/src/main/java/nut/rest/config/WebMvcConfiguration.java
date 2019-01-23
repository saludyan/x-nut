package nut.rest.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import nut.rest.XCustomInterceptors;
import nut.rest.XInterceptorRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Yan on 2017/9/8.
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Autowired(required = false)
    private XCustomInterceptors xCustomInterceptors;

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.extendMessageConverters(converters);
        /**
         * 序列换成json时,将所有的long变成string
         * 因为js中得数字类型不能包含所有的java long值
         */
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        converters.forEach(converter -> {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jackson = (MappingJackson2HttpMessageConverter) converter;
                jackson.getObjectMapper().registerModule(simpleModule);
            }
        });

    }


    /**
     * 统一spring系统异常
     *
     * @return
     */
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> {
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"));
            container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500"));
            container.addErrorPages(new ErrorPage(Throwable.class, "/error/500"));
        };
    }

    /**
     * 支持4种格式
     * 1524450406821
     * Mon Apr 23 10:26:39 CST 2018
     * 2018-04-23 00:00:00
     * 2018-04-23
     *
     * @return
     */
    @Bean
    public Converter string2DateConverter() {
        return new Converter<String, Date>() {

            @Override
            public Date convert(String source) {
                if (source != null && !"".equals(source.trim())) {
                    try {
                        return new Date(new Long(source));
                    } catch (NumberFormatException e) {
                        try {
                            return DateUtil.parseDateTime(source);
                        } catch (Exception e1) {
                            // 处理前端特殊日期格式
                            return DateUtil.parse(source, new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.ENGLISH));
                        }
                    }
                }
                return null;
            }
        };

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (xCustomInterceptors != null) {
            List<XInterceptorRegistration> interceptors = xCustomInterceptors.getInterceptors();

            if (CollUtil.isNotEmpty(interceptors)) {
                interceptors.forEach(lir -> {
                    InterceptorRegistration ir = registry.addInterceptor(lir.getInterceptor());
                    if (lir.getPathMatcher() != null) {
                        ir.pathMatcher(lir.getPathMatcher());
                    }
                    if (CollUtil.isNotEmpty(lir.getExcludePatterns())) {
                        String[] excludePathPatterns = lir.getExcludePatterns().toArray(new String[lir.getExcludePatterns().size()]);
                        ir.excludePathPatterns(excludePathPatterns);
                    }
                    if (CollUtil.isNotEmpty(lir.getIncludePatterns())) {
                        String[] pathPatterns = lir.getIncludePatterns().toArray(new String[lir.getIncludePatterns().size()]);
                        ir.addPathPatterns(pathPatterns);
                    }
                });
            }
        }

        super.addInterceptors(registry);
    }
}


