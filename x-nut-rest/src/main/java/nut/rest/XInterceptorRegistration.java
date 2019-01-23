package nut.rest;

import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Super Yan on 2018/5/22.
 */
@Data
public class XInterceptorRegistration {

    private final HandlerInterceptor interceptor;
    private final List<String> includePatterns = new ArrayList();
    private final List<String> excludePatterns = new ArrayList();
    private PathMatcher pathMatcher;

    public XInterceptorRegistration(HandlerInterceptor interceptor) {
        Assert.notNull(interceptor, "Interceptor is required");
        this.interceptor = interceptor;
    }

    public void addPathPatterns(String... patterns) {
        this.includePatterns.addAll(Arrays.asList(patterns));
    }

    public void excludePathPatterns(String... patterns) {
        this.excludePatterns.addAll(Arrays.asList(patterns));
    }

    public void pathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }


}
