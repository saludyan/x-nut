package nut.jpa.criteria;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import nut.jpa.exceptions.XJpaException;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class XJdbcCriteriaBuilder {

    public static XJdbcType build(String sql, ICriteria criteria) {
        return build(sql,criteria,null,false);
    }

    public static XJdbcType build(String sql, ICriteria criteria, boolean page) {
        return build(sql,criteria,null,page);
    }

    public static XJdbcType build(String sql, ICriteria criteria, String alias, boolean page) {

        String where = "";

        if(!sql.contains("where 1=1")){
            where = sql + " where 1=1 ";
        }else if(!sql.contains("where")){
            where = sql + " where 1=1 ";
        }

        Map<String,Object> parameter = new HashMap();

        Field[] fields = criteria.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();

            Object fieldValue = BeanUtil.getFieldValue(criteria, name);

            if (fieldValue != null) {
                XCriteria xCriteria = field.getAnnotation(XCriteria.class);

                if (xCriteria != null) {
                    String column = xCriteria.column();
                    XCriteriaOperator op = xCriteria.op();
                    XCriteriaCondition cdt = xCriteria.cdt();


                    if (fieldValue instanceof Number ) {
                        Number value = (Number) fieldValue;
                        where += switchOperator(op, buildNormalCondition(column, cdt));
                        parameter.put(column,value);
                    }else if(fieldValue instanceof String){
                        String value = (String) fieldValue;
                        where += switchOperator(op, buildStringCondition(column, cdt));
                        parameter.put(column,value);
                    }else if(fieldValue instanceof Date){
                        Date value = (Date) fieldValue;
                        where += switchOperator(op, buildNormalCondition(column, cdt));
                        parameter.put(column,value);
                    }
                    //TODO 集合
                    else if(fieldValue instanceof Collection) {
                        //Collection collection = (Collection) fieldValue;
//                        booleanExpression = switchOperator(op, buildCollBooleanExpression(realField, cdt, collection));
                    }else{
                        throw new XJpaException("尚未适配,赶紧开发...");
                    }
                }
            }


        }
        String countSql = where;
        String keywordSelect = "select";
        String keywordFrom = "from";
        int startIndex = countSql.indexOf(keywordSelect);
        int endIndex = countSql.indexOf(keywordFrom);

        countSql = countSql.substring(startIndex,keywordSelect.length()+1) + " count(*) " + countSql.substring(endIndex);

        if(page){
            //  limit (pageNumber-1)*pageSize,pageSize
            where += StrUtil.format(" limit {},{} ",criteria.getPageNumber()*criteria.getPageSize(),criteria.getPageSize());
        }

        return new XJdbcType(where,countSql,parameter);
    }

    /**
     * 获取操作符
     * @param op
     * @param condition
     * @return
     */
    private static String switchOperator(XCriteriaOperator op, String condition) {


        switch (op) {
            case or:
                return " or " + condition;
            case and:
                return " and " + condition;
        }
        throw new XJpaException("op is error!");
    }

    private static  String buildStringCondition(String column, XCriteriaCondition cdt) {
        switch (cdt) {
            case eq:
                return StrUtil.indexedFormat(" {0} = :{0} ",column);
            case notEq:
                return StrUtil.indexedFormat(" {0} != :{0} ",column);
            case lessThan:
                return StrUtil.indexedFormat(" {0} < :{0} ",column);
            case lessThanEq:
                return StrUtil.indexedFormat(" {0} < :{0} ",column);
            case greaterThan:
                return StrUtil.indexedFormat(" {0} > :{0} ",column);
            case greaterThanEq:
                return StrUtil.indexedFormat(" {0} >= :{0} ",column);
            case like:
                return StrUtil.indexedFormat(" instr({0},:{0}) > 0 ",column);
            case notLike:
                return StrUtil.indexedFormat(" instr({0},:{0}) = 0 ",column);
            case startWith:
                return StrUtil.indexedFormat(" instr({0},:{0}) = 1 ",column);

        }
        throw new XJpaException("["+cdt.toString()+"] 无法适配");
    }




    private static  BooleanExpression buildCollBooleanExpression(ComparableExpressionBase simple, XCriteriaCondition cdt, Collection collection) {
        BooleanExpression cdtBooleanExpression = null;
        switch (cdt) {
            case in:
                cdtBooleanExpression = simple.in(collection);
                break;
            case notIn:
                cdtBooleanExpression = simple.notIn(collection);
                break;
        }
        return cdtBooleanExpression;
    }


    private static  String buildNormalCondition(String column, XCriteriaCondition cdt) {
        switch (cdt) {
            case eq:
                return StrUtil.indexedFormat(" {0} = :{0} ",column);
            case notEq:
                return StrUtil.indexedFormat(" {0} != :{0} ",column);
            case lessThan:
                return StrUtil.indexedFormat(" {0} < :{0} ",column);
            case lessThanEq:
                return StrUtil.indexedFormat(" {0} <= :{0} ",column);
            case greaterThan:
                return StrUtil.indexedFormat(" {0} > :{0} ",column);
            case greaterThanEq:
                return StrUtil.indexedFormat(" {0} >= :{0} ",column);

        }
        throw new XJpaException("["+cdt.toString()+"] 无法适配");
    }
}
