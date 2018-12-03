package nut.jpa.exceptions;

/**
 * Created by Super Yan on 2018/12/2.
 */
public class XJpaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public XJpaException(String message) {
        super(message);
    }

    public XJpaException(Throwable throwable) {
        super(throwable);
    }

    public XJpaException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
