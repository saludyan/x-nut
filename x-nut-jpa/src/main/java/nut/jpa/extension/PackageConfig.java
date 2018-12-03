package nut.jpa.extension;

import nut.jpa.extension.utils.DbStringUtils;

/**
 * Created by Super Yan on 2018/12/2.
 */
public class PackageConfig {

    /**
     * 父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
     */
    private String parent = "com.x.nut";

    /**
     * 父包模块名。
     */
    private String moduleName = null;

    /**
     * Entity包名
     */
    private String entity = "model";

    /**
     * Service包名
     */
    private String service = "service";

    private String query = "model";

    private String criteria = "criteria";

    /**
     * Service Impl包名
     */
    private String serviceImpl = "service.impl";


    /**
     * Controller包名
     */
    private String controller = "controller";

    public String getParent() {
        if (DbStringUtils.isNotEmpty(moduleName)) {
            return parent + "." + moduleName;
        }
        return parent;
    }

    public PackageConfig setParent(String parent) {
        this.parent = parent;
        return this;
    }

    public String getModuleName() {
        return moduleName;
    }

    public PackageConfig setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public String getEntity() {
        return entity;
    }

    public PackageConfig setEntity(String entity) {
        this.entity = entity;
        return this;
    }

    public String getService() {
        return service;
    }

    public PackageConfig setService(String service) {
        this.service = service;
        return this;
    }

    public String getServiceImpl() {
        return serviceImpl;
    }

    public PackageConfig setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public PackageConfig setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getCriteria() {
        return criteria;
    }

    public PackageConfig setCriteria(String criteria) {
        this.criteria = criteria;
        return this;
    }

    public String getController() {
        if (DbStringUtils.isEmpty(controller)) {
            return "web";
        }
        return controller;
    }

    public PackageConfig setController(String controller) {
        this.controller = controller;
        return this;
    }
}
