package nut.jpa.extension;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import nut.jpa.component.EncryptProvider;
import nut.jpa.exceptions.XJpaException;
import nut.jpa.extension.po.TableFill;
import nut.jpa.extension.rules.DbType;
import nut.jpa.extension.rules.FieldFill;
import nut.jpa.extension.rules.NamingStrategy;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.core.env.Environment;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * spring data jpa & queryDSL 生成器
 * (借鉴mybatis plus源码)
 *
 * Created by Super Yan on 2018/12/2.
 */
public class XSuperGenerator {

    @Data
    @Accessors(chain = true)
    public static class GeneratorConfig{
        private String outputDir="./target/generator".replace("/", File.separator);
        private String dataSourceUserName;
        private String dataSourcePassword;
        private String dataSourceUrl;
        private String packageName;

        private List<String> tablePrefix;
        private List<String> includeTables;
        private List<String> excludeTables;


        public GeneratorConfig setIncludeTables(String...tables){
            this.includeTables = CollUtil.newArrayList(tables);
            return this;
        }

        public GeneratorConfig setExcludeTables(String...tables){
            this.excludeTables = CollUtil.newArrayList(tables);
            return this;
        }

        public GeneratorConfig setTablePrefix(String...freFies){
            this.tablePrefix = CollUtil.newArrayList(freFies);
            return this;
        }
    } 



    public static void generate(GeneratorConfig obj) {

        if(StrUtil.isEmpty(obj.getDataSourceUrl())){
            Map<String,String> ymlMap = getYml();
            String url = ymlMap.get("spring.datasource.url");
            String username = ymlMap.get("spring.datasource.username");
            String password = ymlMap.get("spring.datasource.password");
            if(StrUtil.isBlank(url)){
                throw new XJpaException("请在配置文件配置mysql,或者设置dataSourceUrl");
            }
            if(url.startsWith("ENC(")){
                url = EncryptProvider.decrypt(url);
            }

            if(username.startsWith("ENC(")){
                username = EncryptProvider.decrypt(username);
            }

            if(password.startsWith("ENC(")){
                password = EncryptProvider.decrypt(password);
            }
            obj.setDataSourceUrl(url);
            obj.setDataSourceUserName(username);
            obj.setDataSourcePassword(password);
        }


        DataSourceConfig dataSourceConfig = new DataSourceConfig()
                .setDbType(DbType.MYSQL)
                .setUrl(obj.getDataSourceUrl())
                .setUsername(obj.getDataSourceUserName())
                .setPassword(obj.getDataSourcePassword())
                .setDriverName("com.mysql.jdbc.Driver");

        StrategyConfig strategyConfig = new StrategyConfig()
                .setCapitalMode(true)
                .setEntityLombokModel(true)
                .setDbColumnUnderline(true)
                .setTablePrefix(obj.getTablePrefix()!=null?ArrayUtil.toArray(obj.getTablePrefix(),String.class):null)
                .setNaming(NamingStrategy.underline_to_camel)
                .setInclude(obj.getIncludeTables()!=null?ArrayUtil.toArray(obj.getIncludeTables(),String.class):null)//修改替换成你需要的表名，多个表名传数组
                .setExclude(obj.getExcludeTables()!=null?ArrayUtil.toArray(obj.getExcludeTables(),String.class):null)
                .setVersionFieldName("version")//默认乐观锁字段
                .setTableFillList(Arrays.asList(
                        new TableFill("version", FieldFill.INSERT),
                        new TableFill("status", FieldFill.INSERT),
                        new TableFill("create_time", FieldFill.INSERT),
                        new TableFill("update_time", FieldFill.INSERT),
                        new TableFill("creator", FieldFill.INSERT),
                        new TableFill("updator", FieldFill.INSERT_UPDATE)));

        GlobalConfig config = new GlobalConfig()
                .setActiveRecord(false)
                .setEnableCache(false)
                .setAuthor("x-nut")
                .setServiceName("%sService")
                .setOutputDir(obj.getOutputDir())
                .setFileOverride(true)
                .setBaseResultMap(true);

        new AutoGenerator()
                .setGlobalConfig(config)
                .setDataSource(dataSourceConfig)
                .setStrategy(strategyConfig)
                .setPackageInfo(new PackageConfig().setParent(obj.getPackageName()))
                .execute();
    }

    private static Map<String,String> getYml(){
        ClassPathResource resource = new ClassPathResource("application.yml");
        Yaml yaml = new Yaml();
        Iterator<Object> result = yaml.loadAll(resource.getStream()).iterator();
        Map<String,String> sourceMap = new HashMap<>();
        while(result.hasNext()){
            Map map=(Map)result.next();
            iteratorYml( sourceMap,map,null);
        }
        return sourceMap;
    }

    public static void iteratorYml(Map<String,String> sourceMap,Map map,String key) {
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key2 = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof LinkedHashMap) {
                if (key == null) {
                    iteratorYml(sourceMap,(Map) value, key2.toString());
                } else {
                    iteratorYml(sourceMap,(Map) value, key + "." + key2.toString());
                }
            }
            if (value instanceof String) {
                if (key == null) {
                    sourceMap.put(key2.toString(), value.toString());
                }
                if (key != null) {
                    sourceMap.put(key + "." + key2.toString(), value.toString());
                }
            }
        }

    }



}
