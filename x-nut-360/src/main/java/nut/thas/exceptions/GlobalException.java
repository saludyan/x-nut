package nut.thas.exceptions;

/**
 * Created by Super Yan on 2018/11/29.
 */
public interface GlobalException {

    String getCode();
    String getEnglish();
    String getChinese();
    String[] getParameters();
    String getErrorMsg();
}
