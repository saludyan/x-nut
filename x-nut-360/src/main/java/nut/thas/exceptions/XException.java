package nut.thas.exceptions;

/**
 * Created by Super Yan on 2018/12/2.
 */
public class XException extends RuntimeException {



    private String errorCode;

    public XException() {
        super();
    }

    public XException(GlobalException message) {
        super(message.getErrorMsg());
        errorCode = message.getCode();
    }


    public XException(String message) {
        super(message);
    }

    public XException(String message, Throwable cause) {
        super(message, cause);
    }

    public XException(Throwable cause) {
        super(cause);
    }

    protected XException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
