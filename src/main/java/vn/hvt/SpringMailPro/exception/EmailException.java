package vn.hvt.SpringMailPro.exception;

public class EmailException extends RuntimeException {
    private ErrorCode errorCode;

    public EmailException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public EmailException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public EmailException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public EmailException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
