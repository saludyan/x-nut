package nut.jpa.identifier;

import cn.hutool.core.util.RandomUtil;
import nut.jpa.component.AppCtxProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * Created by Yan on 2017/8/18.
 */
public class IDSequence {

    private static Logger log = LoggerFactory.getLogger(IDSequence.class);

    private static Sequence sequence;

    public static Sequence Instance(){

        if (sequence == null){
            Environment env = null;
            try{
                env = AppCtxProvider.getBean(Environment.class);
            }catch (Exception e){
                log.trace("获取 Environment 失败,随机生成1~31");
            }


            long randomWorkId = RandomUtil.randomLong(31);
            long randomDataCenterId = RandomUtil.randomLong(31);
            String str_workId = env!=null?env.getProperty("app.sequence.workId"):null;
            String str_dataCenterId = env!=null?env.getProperty("app.sequence.dataCenterId"):null;
            long workId = StringUtils.isBlank(str_workId)?randomWorkId:Long.valueOf(str_workId);
            long dataCenterId = StringUtils.isBlank(str_dataCenterId)?randomDataCenterId:Long.valueOf(str_dataCenterId);
            sequence = new Sequence(workId,dataCenterId);
        }
        return sequence;
    }
}
