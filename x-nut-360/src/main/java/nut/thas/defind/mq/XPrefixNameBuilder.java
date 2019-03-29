package nut.thas.defind.mq;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: Yan
 * @date 2019-03-27
 * @Version: 1.0
 */
@Component
public class XPrefixNameBuilder {

    private static String PREFIX="x.nut.mq.";

    public XPrefixNameBuilder(@Value("${x.nut.mq.prefixName:}") String prefix){
        if(StrUtil.isNotBlank(prefix)){
            PREFIX = prefix;
        }
    }

    public static String build(String suffix){
        if(StrUtil.isBlank(suffix)){
            return "";
        }
        return PREFIX + suffix;
    }
}
