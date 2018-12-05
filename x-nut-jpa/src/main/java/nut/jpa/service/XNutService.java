package nut.jpa.service;

import cn.hutool.core.collection.IterUtil;
import nut.jpa.criteria.ICriteria;
import nut.jpa.criteria.XPage;
import nut.jpa.model.XNutModel;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface XNutService<Entity extends XNutModel> {

    long count(ICriteria criteria);

    boolean exists(ICriteria criteria);

    List<Entity> find(ICriteria criteria);

    XPage<Entity> findPage(ICriteria criteria);

    Entity get(ICriteria criteria);

    Entity save(Entity entity);

    List<Entity> save(List<Entity> entities);

    Entity get(Long id);

    // delete 物理删除
    void delete(Long id);

    void delete(ICriteria criteria);

    void delete(Entity entity);

    void delete(Set<Long> ids);

    // trash 逻辑删除
    void trash(Long id);

    void trash(ICriteria criteria);

    void trash(Entity entity);

    void trash(Set<Long> ids);
}
