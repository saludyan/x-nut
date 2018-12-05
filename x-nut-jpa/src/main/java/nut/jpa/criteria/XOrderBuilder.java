package nut.jpa.criteria;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import nut.jpa.exceptions.XJpaException;

import java.lang.reflect.Field;
import java.util.*;

public class XOrderBuilder {

    public static List<OrderSpecifier<?>> buildList(ICriteria criteria) {

        EntityPathBase q = criteria.getQEntity();


        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        Field[] fields = criteria.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();

            Object fieldValue =  BeanUtil.getFieldValue(criteria, name);

            if (fieldValue!=null) {
                XOrder xOrder = field.getAnnotation(XOrder.class);
                if (xOrder != null) {
                    String value = (String) fieldValue;
                    if(StrUtil.isNotBlank(value)){
                        String column = xOrder.column();
                        ComparableExpressionBase realField = (ComparableExpressionBase) BeanUtil.getProperty(q, column);
                        if(value.toLowerCase().equals("asc")){
                            orderSpecifiers.add(realField.asc());
                        }else if(value.toLowerCase().equals("desc")){
                            orderSpecifiers.add(realField.desc());
                        }else{
                            throw new XJpaException("排序字段值只能是[asc,desc]");
                        }
                    }

                }
            }
        }

        return orderSpecifiers;
    }

    public static OrderSpecifier[] build(ICriteria criteria) {

        List<OrderSpecifier<?>> orderSpecifiers = buildList(criteria);

        OrderSpecifier[] array = new OrderSpecifier[orderSpecifiers.size()];
        for (int i = 0; i < orderSpecifiers.size(); i++) {
            array[i] = orderSpecifiers.get(i);
        }

        return array;
    }
}
