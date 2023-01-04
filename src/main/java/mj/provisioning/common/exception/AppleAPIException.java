package mj.provisioning.common.exception;

public class AppleAPIException extends RuntimeException{

    public AppleAPIException() {
        super();
    }

    public AppleAPIException(String message) {
        super(message);
    }

    public AppleAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppleAPIException(Throwable cause) {
        super(cause);
    }

    protected AppleAPIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
