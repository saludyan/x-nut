package nut.jpa.criteria;

import cn.hutool.core.bean.BeanUtil;
import com.querydsl.core.types.dsl.*;
import nut.jpa.exceptions.XJpaException;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;

public class XCriteriaBuilder {

    public static BooleanExpression build(ICriteria criteria) {

        EntityPathBase q = criteria.getQEntity();

        BooleanExpression booleanExpression = null;


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


                    if (fieldValue instanceof Number) {
                        NumberPath realField = (NumberPath) BeanUtil.getProperty(q, column);
                        Number value = (Number) fieldValue;
                        booleanExpression = switchOperator(booleanExpression, op, buildNumberBooleanExpression(realField, cdt, value));
                    }else if(fieldValue instanceof String){
                        StringPath realField = (StringPath) BeanUtil.getProperty(q, column);
                        String value = (String) fieldValue;
                        booleanExpression = switchOperator(booleanExpression, op, buildStringBooleanExpression(realField, cdt, value));
                    }else if(fieldValue instanceof Date){
                        DatePath realField = (DatePath) BeanUtil.getProperty(q, column);
                        Date value = (Date) fieldValue;
                        booleanExpression = switchOperator(booleanExpression, op, buildDateBooleanExpression(realField, cdt, value));
                    }
                    // 集合
                    else if(fieldValue instanceof Collection) {
                        Collection collection = (Collection) fieldValue;
                        ComparableExpressionBase realField = (ComparableExpressionBase) BeanUtil.getProperty(q, column);
                        booleanExpression = switchOperator(booleanExpression, op, buildCollBooleanExpression(realField, cdt, collection));
                    }else{
                        throw new XJpaException("尚未适配,赶紧开发...");
                    }
                }
            }
        }

        return booleanExpression;
    }

    /**
     * 获取操作符
     * @param base
     * @param op
     * @param opBe
     * @return
     */
    private static BooleanExpression switchOperator(BooleanExpression base, XCriteriaOperator op, BooleanExpression opBe) {
        if (base == null) {
            return opBe;
        }

        switch (op) {
            case or:
                return base.or(opBe);
            case and:
                return base.and(opBe);
        }
        throw new XJpaException("op is error!");
    }

    private static  BooleanExpression buildStringBooleanExpression(StringPath stringPath, XCriteriaCondition cdt, String str) {
        BooleanExpression cdtBooleanExpression = null;
        switch (cdt) {
            case eq:
                return stringPath.eq(str);
            case notEq:
                cdtBooleanExpression = stringPath.ne(str);
                break;
            case lessThan:
                cdtBooleanExpression = stringPath.lt(str);
                break;
            case lessThanEq:
                cdtBooleanExpression = stringPath.loe(str);
                break;
            case greaterThan:
                cdtBooleanExpression = stringPath.gt(str);
                break;
            case greaterThanEq:
                cdtBooleanExpression = stringPath.goe(str);
                break;
            case like:
                cdtBooleanExpression = stringPath.lower().indexOf(str.trim().toLowerCase()).goe(0);
                break;
            case notLike:
                cdtBooleanExpression = stringPath.lower().indexOf(str.trim().toLowerCase()).eq(-1);
                break;
            case startWith:
                cdtBooleanExpression = stringPath.lower().startsWithIgnoreCase(str.trim());

        }
        return cdtBooleanExpression;
    }


    private static  BooleanExpression buildNumberBooleanExpression(NumberPath numberPath, XCriteriaCondition cdt, Number number) {
        BooleanExpression cdtBooleanExpression = null;
        switch (cdt) {
            case eq:
                return numberPath.eq(number);
            case notEq:
                cdtBooleanExpression = numberPath.ne(number);
                break;
            case lessThan:
                cdtBooleanExpression = numberPath.lt(number);
                break;
            case lessThanEq:
                cdtBooleanExpression = numberPath.loe(number);
                break;
            case greaterThan:
                cdtBooleanExpression = numberPath.gt(number);
                break;
            case greaterThanEq:
                cdtBooleanExpression = numberPath.goe(number);
                break;

        }
        return cdtBooleanExpression;
    }



    private static  BooleanExpression buildDateBooleanExpression(DatePath datePath, XCriteriaCondition cdt, Date date) {
        BooleanExpression cdtBooleanExpression = null;
        switch (cdt) {
            case eq:
                return datePath.eq(date);
            case notEq:
                cdtBooleanExpression = datePath.ne(date);
                break;
            case lessThan:
                cdtBooleanExpression = datePath.before(date);
                break;
            case lessThanEq:
                cdtBooleanExpression = datePath.loe(date);
                break;
            case greaterThan:
                cdtBooleanExpression = datePath.after(date);
                break;
            case greaterThanEq:
                cdtBooleanExpression = datePath.goe(date);
                break;
        }
        return cdtBooleanExpression;
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
}
