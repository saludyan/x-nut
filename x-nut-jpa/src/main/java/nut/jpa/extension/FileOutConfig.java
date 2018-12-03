package nut.jpa.extension;

import nut.jpa.extension.po.TableInfo;

/**
 *
 * 输出文件配置
 * Created by Super Yan on 2018/12/2.
 */
public abstract class FileOutConfig {

    /**
     * 模板
     */
    private String templatePath;

    public FileOutConfig() {
        // to do nothing
    }

    public FileOutConfig(String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * 输出文件
     */
    public abstract String outputFile(TableInfo tableInfo);

    public String getTemplatePath() {
        return templatePath;
    }

    public FileOutConfig setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
        return this;
    }
}
