package nut.rest.exception;

import cn.hutool.core.util.StrUtil;
import nut.thas.exceptions.GlobalException;

/**
 * Created by Super Yan on 2018/11/29.
 */
public enum XRestException implements GlobalException {

    E4001("4001", "System error", "系统错误"),;

    private String code;
    private String english;
    private String chinese;
    private String[] parameters;

    private XRestException(String code, String english, String chinese) {
        this.code = code;
        this.english = english;
        this.chinese = chinese;
    }

    public String getEnglish() {
        return StrUtil.isBlank(this.english) ? this.chinese : this.english;
    }

    public String getChinese() {
        return StrUtil.isBlank(this.chinese) ? this.english : this.chinese;
    }



    public String getErrorMsg() {
        String msg = StrUtil.isBlank(this.english) ? this.chinese : this.english;
        return this.parameters != null ? StrUtil.format(msg, this.parameters) : msg;
    }

    public String getErrorMsg(String... args) {
        String msg = this.english;
        if (StrUtil.isBlank(this.english)) {
            msg = this.chinese;
        }

        return StrUtil.format(msg, args);
    }

    public GlobalException setParameter(String... args) {
        this.parameters = args;
        return this;
    }

    public String getCode() {
        return this.code;
    }

    public String[] getParameters() {
        return this.parameters;
    }

}
