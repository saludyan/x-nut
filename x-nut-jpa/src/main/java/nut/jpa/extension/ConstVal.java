package nut.jpa.extension;

import java.nio.charset.Charset;

/**
 * Created by Super Yan on 2018/12/2.
 */
public class ConstVal {

    public static final String MODULENAME = "ModuleName";

    public static final String ENTITY = "Entity";
    public static final String QUERY = "Query";
    public static final String CRITERIA = "Criteria";
    public static final String SERIVCE = "Service";
    public static final String SERVICEIMPL = "ServiceImpl";
    public static final String CONTROLLER = "Controller";

    public static final String ENTITY_PATH = "entity_path";
    public static final String QUERY_PATH = "query_path";
    public static final String CRITERIA_PATH = "criteria_path";
    public static final String SERIVCE_PATH = "serivce_path";
    public static final String SERVICEIMPL_PATH = "serviceimpl_path";
    public static final String CONTROLLER_PATH = "controller_path";

    public static final String JAVA_TMPDIR = "java.io.tmpdir";
    public static final String UTF8 = Charset.forName("UTF-8").name();
    public static final String UNDERLINE = "_";

    public static final String JAVA_SUFFIX = ".java";
    public static final String KT_SUFFIX = ".kt";

    public static final String TEMPLATE_ENTITY_JAVA = "/templates/entity.java";
    public static final String TEMPLATE_QUERY_JAVA = "/templates/query.java";
    public static final String TEMPLATE_CRITERIA_JAVA = "/templates/criteria.java";
    public static final String TEMPLATE_SERVICE = "/templates/service.java";
    public static final String TEMPLATE_SERVICEIMPL = "/templates/serviceImpl.java";
    public static final String TEMPLATE_CONTROLLER = "/templates/controller.java";

    public static final String VM_LOADPATH_KEY = "file.resource.loader.class";
    public static final String VM_LOADPATH_VALUE = "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";

    public static final String SUPERD_SERVICE_CLASS = "com.x.nut.service.IService";
    public static final String SUPERD_SERVICEIMPL_CLASS = "com.x.nut.service.impl.ServiceImpl";

    /**
     * 输出相关常量
     */
    public static final String OUT_CONFIG = "config";
}
