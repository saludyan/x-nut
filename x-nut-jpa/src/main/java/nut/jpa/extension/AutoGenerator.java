package nut.jpa.extension;

import nut.jpa.extension.builder.ConfigBuilder;
import nut.jpa.extension.engine.AbstractTemplateEngine;
import nut.jpa.extension.engine.VelocityTemplateEngine;
import nut.jpa.extension.po.TableField;
import nut.jpa.extension.po.TableInfo;
import nut.jpa.extension.utils.DbStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Super Yan on 2018/12/2.
 */
public class AutoGenerator {

    private static final Logger logger = LoggerFactory.getLogger(AutoGenerator.class);
    /**
     * 配置信息
     */
    protected ConfigBuilder config;
    /**
     * 注入配置
     */
    protected InjectionConfig injectionConfig;
    /**
     * 数据源配置
     */
    private DataSourceConfig dataSource;
    /**
     * 数据库表配置
     */
    private StrategyConfig strategy;
    /**
     * 包 相关配置
     */
    private PackageConfig packageInfo;
    /**
     * 模板 相关配置
     */
    private TemplateConfig template;
    /**
     * 全局 相关配置
     */
    private GlobalConfig globalConfig;
    /**
     * 模板引擎
     */
    private AbstractTemplateEngine templateEngine;

    /**
     * 生成代码
     */
    public void execute() {
        logger.debug("==========================准备生成文件...==========================");
        // 初始化配置
        if (null == config) {
            config = new ConfigBuilder(packageInfo, dataSource, strategy, template, globalConfig);
            if (null != injectionConfig) {
                injectionConfig.setConfig(config);
            }
        }
        if (null == templateEngine) {
            // 为了兼容之前逻辑，采用 Velocity 引擎 【 默认 】
            templateEngine = new VelocityTemplateEngine();
        }
        // 模板引擎初始化执行文件输出
        templateEngine.init(this.pretreatmentConfigBuilder(config)).mkdirs().batchOutput().open();
        logger.debug("==========================文件生成完成！！！==========================");
    }

    /**
     * <p>
     * 开放表信息、预留子类重写
     * </p>
     *
     * @param config 配置信息
     * @return
     */
    protected List<TableInfo> getAllTableInfoList(ConfigBuilder config) {
        return config.getTableInfoList();
    }

    /**
     * <p>
     * 预处理配置
     * </p>
     *
     * @param config 总配置信息
     * @return 解析数据结果集
     */
    protected ConfigBuilder pretreatmentConfigBuilder(ConfigBuilder config) {
        /**
         * 注入自定义配置
         */
        if (null != injectionConfig) {
            injectionConfig.initMap();
            config.setInjectionConfig(injectionConfig);
        }
        /**
         * 表信息列表
         */
        List<TableInfo> tableList = this.getAllTableInfoList(config);
        for (TableInfo tableInfo : tableList) {
            /* ---------- 添加导入包 ---------- */
            if (config.getGlobalConfig().isActiveRecord()) {
                // 开启 ActiveRecord 模式
//TODO                tableInfo.setImportPackages(Model.class.getCanonicalName());
            }
            if (tableInfo.isConvert()) {
                // 表注解
//TODO                tableInfo.setImportPackages(TableName.class.getCanonicalName());
            }
            if (tableInfo.isLogicDelete(config.getStrategyConfig().getLogicDeleteFieldName())) {
                // 逻辑删除注解
//TODO                tableInfo.setImportPackages(TableLogic.class.getCanonicalName());
            }
            if (DbStringUtils.isNotEmpty(config.getStrategyConfig().getVersionFieldName())) {
                // 乐观锁注解
//TODO                tableInfo.setImportPackages(Version.class.getCanonicalName());
            }
            if (DbStringUtils.isNotEmpty(config.getSuperEntityClass())) {
                // 父实体
                tableInfo.setImportPackages(config.getSuperEntityClass());
            } else {
                tableInfo.setImportPackages(Serializable.class.getCanonicalName());
            }
            // Boolean类型is前缀处理
            if (config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix()) {
                for (TableField field : tableInfo.getFields()) {
                    if (field.getPropertyType().equalsIgnoreCase("boolean")) {
                        if (field.getPropertyName().startsWith("is")) {
                            field.setPropertyName(config.getStrategyConfig(),
                                    DbStringUtils.removePrefixAfterPrefixToLower(field.getPropertyName(), 2));
                        }
                    }
                }
            }
        }
        return config.setTableInfoList(tableList);
    }

    // ==================================  相关配置  ==================================

    public DataSourceConfig getDataSource() {
        return dataSource;
    }

    public AutoGenerator setDataSource(DataSourceConfig dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public StrategyConfig getStrategy() {
        return strategy;
    }

    public AutoGenerator setStrategy(StrategyConfig strategy) {
        this.strategy = strategy;
        return this;
    }

    public PackageConfig getPackageInfo() {
        return packageInfo;
    }

    public AutoGenerator setPackageInfo(PackageConfig packageInfo) {
        this.packageInfo = packageInfo;
        return this;
    }

    public TemplateConfig getTemplate() {
        return template;
    }

    public AutoGenerator setTemplate(TemplateConfig template) {
        this.template = template;
        return this;
    }

    public ConfigBuilder getConfig() {
        return config;
    }

    public AutoGenerator setConfig(ConfigBuilder config) {
        this.config = config;
        return this;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public AutoGenerator setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }

    public InjectionConfig getCfg() {
        return injectionConfig;
    }

    public AutoGenerator setCfg(InjectionConfig injectionConfig) {
        this.injectionConfig = injectionConfig;
        return this;
    }

    public AbstractTemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    public AutoGenerator setTemplateEngine(AbstractTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        return this;
    }
}
