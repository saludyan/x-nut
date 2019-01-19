package nut.jpa.service.impl;

import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import nut.jpa.criteria.ICriteria;
import nut.jpa.criteria.XJdbcCriteriaBuilder;
import nut.jpa.criteria.XJdbcType;
import nut.jpa.criteria.XPage;
import nut.jpa.exceptions.XJpaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

public abstract class XJdbcExpander {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    protected <T> XPage<T> findPage(String[] sql, ICriteria criteria, String alias, RowMapper<T> rowMapper){

        String assemblySql = "";

        for (String s : sql) {
            assemblySql += StrUtil.format(" {} ",s);
        }

        XJdbcType xJdbcType = XJdbcCriteriaBuilder.build(assemblySql,criteria,alias,true);

        NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(jdbcTemplate);

        // 获取总记录
        String processSql =  xJdbcType.getProcessSql();
        String countSql = xJdbcType.getCountSql();
        Map<String,Object> parameters = xJdbcType.getParameters();

        int total = jdbc.queryForObject(countSql,parameters,Integer.class);

        List<T> list = jdbc.query(processSql,parameters,rowMapper);

        XPage<T> xPage = new XPage<>();

        int totalPages = PageUtil.totalPage(total,criteria.getPageSize());

        xPage.setPageNumber(criteria.getPageNumber());
        xPage.setPageSize(criteria.getPageSize());
        xPage.setTotal(total);
        xPage.setTotalPages(totalPages);
        xPage.setContent(list);
        xPage.setHasNext((criteria.getPageNumber()+1)<totalPages);

        //TODO 排序未做

        return xPage;
    }

    protected <T> XPage<T> find(String[] sql, ICriteria criteria, RowMapper<T> rowMapper){
        throw new XJpaException("方法未写完....");
    }


}
