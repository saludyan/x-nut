package nut.security.exceptions;

/**
 * Created by Super Yan on 2018/12/2.
 */
public class XSecurityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public XSecurityException(String message) {
        super(message);
    }

    public XSecurityException(Throwable throwable) {
        super(throwable);
    }

    public XSecurityException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
