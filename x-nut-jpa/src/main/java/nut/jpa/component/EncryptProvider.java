package nut.jpa.component;

import cn.hutool.core.util.StrUtil;
import nut.jpa.exceptions.XJpaException;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

import java.lang.management.ManagementFactory;
import java.util.Map;

public class EncryptProvider {


    private static StringEncryptor stringEncryptor;
    private static StringEncryptor stringEncryptor() {
        if(stringEncryptor == null){
            Map<String,String> map = ManagementFactory.getRuntimeMXBean().getSystemProperties();
            String password = map.get("jasypt.encryptor.password");

            if(StrUtil.isBlank(password)){
                throw new XJpaException("请在运行参数(VM options)添加-Djasypt.encryptor.password=***");
            }

            PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            SimpleStringPBEConfig config = new SimpleStringPBEConfig();
            config.setPassword(password);
            config.setAlgorithm("PBEWithMD5AndDES");
            config.setKeyObtentionIterations("1000");
            config.setPoolSize("1");
            config.setProviderName(null);
            config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
            config.setStringOutputType("base64");
            encryptor.setConfig(config);
            stringEncryptor = encryptor;
        }

        return stringEncryptor;
    }


    public static String encrypt(String message){
        return stringEncryptor().encrypt(message);
    }



    public static String decrypt(String encryptedMessage){
        String  msg = null;

        if(encryptedMessage.startsWith("ENC(")) {
            msg = encryptedMessage.substring(4, encryptedMessage.length() - 1);
        }
        return stringEncryptor().decrypt(msg);
    }
}
