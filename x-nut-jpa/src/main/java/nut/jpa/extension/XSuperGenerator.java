package nut.jpa.extension;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import nut.jpa.exceptions.XJpaException;
import nut.jpa.extension.po.TableFill;
import nut.jpa.extension.rules.DbType;
import nut.jpa.extension.rules.FieldFill;
import nut.jpa.extension.rules.NamingStrategy;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.*;

/**
 * spring data jpa & queryDSL 生成器
 * (借鉴mybatis plus源码)
 *
 * Created by Super Yan on 2018/12/2.
 */
public class XSuperGenerator {

    @Data
    public static class XSuperGeneratorObj{
        private String outputDir="./target/generator".replace("/", File.separator);
        private String dataSourceUserName;
        private String dataSourcePassword;
        private String dataSourceUrl;
        private String packageName;

        private String[] tablePrefix;
        private String[] includeTables;
        private String[] excludeTables;
    } 



    public static void generate(XSuperGeneratorObj obj) {

        if(StrUtil.isEmpty(obj.getDataSourceUrl())){
            Map<String,String> ymlMap = getYml();
            String url = ymlMap.get("spring.datasource.url");
            String username = ymlMap.get("spring.datasource.username");
            String password = ymlMap.get("spring.datasource.password");
            if(StrUtil.isBlank(url)){
                throw new XJpaException("请在配置文件配置mysql,或者设置dataSourceUrl");
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
                .setTablePrefix(obj.getTablePrefix())
                .setNaming(NamingStrategy.underline_to_camel)
                .setInclude(obj.getIncludeTables())//修改替换成你需要的表名，多个表名传数组
                .setExclude(obj.getExcludeTables())
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
