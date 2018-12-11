package nut.safe.exceptions;

/**
 * Created by Super Yan on 2018/12/2.
 */
public class XSafeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public XSafeException(String message) {
        super(message);
    }

    public XSafeException(Throwable throwable) {
        super(throwable);
    }

    public XSafeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
