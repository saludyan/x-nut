package nut.jpa.extension.po;

import nut.jpa.extension.rules.FieldFill;

/**
 * Created by Super Yan on 2018/12/2.
 */
public class TableFill {

    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 忽略类型
     */
    private FieldFill fieldFill;

    private TableFill() {
        // to do nothing
    }

    public TableFill(String fieldName, FieldFill ignore) {
        this.fieldName = fieldName;
        this.fieldFill = ignore;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public FieldFill getFieldFill() {
        return fieldFill;
    }

    public void setFieldFill(FieldFill fieldFill) {
        this.fieldFill = fieldFill;
    }
}
