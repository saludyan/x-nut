package nut.jpa.extension;

import nut.jpa.extension.rules.DbColumnType;

/**
 * Created by Super Yan on 2018/12/2.
 */
public interface ITypeConvert {

    /**
     * <p>
     * 执行类型转换
     * </p>
     *
     * @param fieldType 字段类型
     * @return
     */
    DbColumnType processTypeConvert(String fieldType);
}
