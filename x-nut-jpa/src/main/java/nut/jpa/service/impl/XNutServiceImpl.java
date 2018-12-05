package nut.jpa.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.util.ArrayUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import nut.jpa.config.AppCtxProvider;
import nut.jpa.criteria.ICriteria;
import nut.jpa.criteria.XCriteriaBuilder;
import nut.jpa.criteria.XOrderBuilder;
import nut.jpa.criteria.XPage;
import nut.jpa.model.XNutModel;
import nut.jpa.service.XNutService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

public abstract class XNutServiceImpl<Entity extends XNutModel> implements XNutService<Entity> {

    private QueryDslPredicateExecutor<Entity> executor;

    private CrudRepository<Entity, Long> crudRepository;

    @Override
    public long count(ICriteria criteria) {
        BooleanExpression booleanExpression = XCriteriaBuilder.build(criteria);
        return this.getExecutor().count(booleanExpression);
    }

    @Override
    public boolean exists(ICriteria criteria) {
        BooleanExpression booleanExpression = XCriteriaBuilder.build(criteria);
        return this.getExecutor().exists(booleanExpression);
    }

    @Override
    public List<Entity> find(ICriteria criteria) {
        BooleanExpression booleanExpression = XCriteriaBuilder.build(criteria);
        OrderSpecifier[] orderSpecifiers = XOrderBuilder.build(criteria);
        Iterable<Entity> iterable = null;
        if (ArrayUtil.isNotEmpty(orderSpecifiers)) {
            iterable = this.getExecutor().findAll(booleanExpression, orderSpecifiers);
        } else {
            iterable = this.getExecutor().findAll(booleanExpression);
        }

        return IterUtil.toList(iterable);
    }

    @Override
    public XPage<Entity> findPage(ICriteria criteria) {
        BooleanExpression booleanExpression = XCriteriaBuilder.build(criteria);
        List<OrderSpecifier<?>> orderSpecifiers = XOrderBuilder.buildList(criteria);
        Pageable pageable = null;
        if (CollUtil.isNotEmpty(orderSpecifiers)) {
            pageable = new QPageRequest(criteria.getPageNumber(), criteria.getPageSize(), new QSort(orderSpecifiers));
        } else {
            pageable = new PageRequest(criteria.getPageNumber(), criteria.getPageSize());
        }

        Page<Entity> page = this.getExecutor().findAll(booleanExpression, pageable);
        return XPage.build(page);
    }

    @Override
    public Entity get(ICriteria criteria) {
        BooleanExpression booleanExpression = XCriteriaBuilder.build(criteria);
        return (Entity) this.getExecutor().findOne(booleanExpression);
    }

    @Override
    @Transactional
    public Entity save(Entity entity) {
        return this.getRepository().save(entity);
    }

    @Override
    @Transactional
    public List<Entity> save(List<Entity> entities) {
        Iterable<Entity> savedEntities = this.getRepository().save(entities);
        return IterUtil.toList(savedEntities);
    }

    @Override
    public Entity get(Long id) {
        return this.getRepository().findOne(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        this.getRepository().delete(id);
    }

    @Override
    @Transactional
    public void delete(ICriteria criteria) {
        List<Entity> entities = this.find(criteria);
        this.getRepository().delete(entities);
    }

    @Override
    @Transactional
    public void delete(Entity entity) {
        this.getRepository().delete(entity);
    }

    @Override
    @Transactional
    public void delete(Set<Long> ids) {
        ids.forEach(id -> this.getRepository().delete(id));
    }

    @Override
    @Transactional
    public void trash(Long id) {
        Entity entity = this.get(id);
        entity.setStatus(0);
        this.save(entity);
    }

    @Override
    @Transactional
    public void trash(ICriteria criteria) {
        Entity entity = this.get(criteria);
        entity.setStatus(0);
        this.save(entity);
    }

    @Override
    @Transactional
    public void trash(Entity entity) {
        Long id =entity.getId();
        this.trash(id);
    }

    @Override
    @Transactional
    public void trash(Set<Long> ids) {
        ids.forEach(id->{
            this.trash(id);
        });
    }

    private QueryDslPredicateExecutor getExecutor() {
        if (executor == null) {

            executor = (QueryDslPredicateExecutor<Entity>) AppCtxProvider.getBean(getRepositoryName());
        }
        return executor;
    }

    private CrudRepository<Entity, Long> getRepository() {
        if (crudRepository == null) {
            crudRepository = (CrudRepository<Entity, Long>) AppCtxProvider.getBean(getRepositoryName());
        }
        return crudRepository;
    }

    private String getRepositoryName(){
        String typeName = ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
        typeName = typeName.substring(typeName.lastIndexOf(".")+1);
        String repositoryName = typeName.substring(0,1).toLowerCase() + typeName.substring(1) + "Repository";
        return repositoryName;

    }

}
