package nut.jpa.extension;

import lombok.Data;
import nut.jpa.extension.po.TableFill;
import nut.jpa.extension.po.TableInfo;
import nut.jpa.extension.rules.DbType;
import nut.jpa.extension.rules.FieldFill;
import nut.jpa.extension.rules.NamingStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public static void main(String[] args) {
        XSuperGeneratorObj obj = new XSuperGeneratorObj();
        obj.setDataSourceUserName("yan");
        obj.setDataSourcePassword("yan123");
        obj.setDataSourceUrl("jdbc:mysql://120.79.100.24:3306/locals_yan");
        obj.setTablePrefix(new String[]{"example_"});
        obj.setPackageName("nut.example");
        XSuperGenerator.generate(obj);
    }

    public static void generate(XSuperGeneratorObj obj) {
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
}
