package nut.jpa.extension;

/**
 * 模板路径配置项
 *
 * Created by Super Yan on 2018/12/2.
 */
public class TemplateConfig {
    private String entity = ConstVal.TEMPLATE_ENTITY_JAVA;

    private String query = ConstVal.TEMPLATE_QUERY_JAVA;

    private String criteria = ConstVal.TEMPLATE_CRITERIA_JAVA;

    private String repository = ConstVal.TEMPLATE_REPOSITORY_JAVA;

    private String service = ConstVal.TEMPLATE_SERVICE;

    private String serviceImpl = ConstVal.TEMPLATE_SERVICEIMPL;

    private String controller = ConstVal.TEMPLATE_CONTROLLER;

    public String getEntity() {
        return entity;
    }

    public TemplateConfig setEntity(String entity) {
        this.entity = entity;
        return this;
    }

    public String getService() {
        return service;
    }

    public TemplateConfig setService(String service) {
        this.service = service;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public TemplateConfig setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getCriteria() {
        return criteria;
    }

    public String getRepository() {
        return repository;
    }

    public TemplateConfig setRepository(String repository) {
        this.repository = repository;
        return this;
    }

    public TemplateConfig setCriteria(String criteria) {
        this.criteria = criteria;
        return this;
    }

    public String getServiceImpl() {
        return serviceImpl;
    }

    public TemplateConfig setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
        return this;
    }


    public String getController() {
        return controller;
    }

    public TemplateConfig setController(String controller) {
        this.controller = controller;
        return this;
    }
}
