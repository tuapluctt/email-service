package vn.hvt.SpringMailPro.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    // Lỗi chung
    EMAIL_GENERAL_ERROR(2000, "Unknown error sending email", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_EMAIL_REQUEST(2001, "invalid email", HttpStatus.BAD_REQUEST),

    // Lỗi cấu hình
    EMAIL_CONFIG_ERROR(2100, "Email service configuration error", HttpStatus.SERVICE_UNAVAILABLE),
    PROVIDER_NOT_AVAILABLE(2101, "No email providers are available", HttpStatus.SERVICE_UNAVAILABLE),

    // Lỗi template
    TEMPLATE_ERROR(2200, "Error processing template email", HttpStatus.INTERNAL_SERVER_ERROR),
    TEMPLATE_NOT_FOUND(2201, "Template not found", HttpStatus.NOT_FOUND),


    // Lỗi SMTP
    SMTP_CONNECTION_ERROR(2300, "Unable to connect to SMTP server", HttpStatus.SERVICE_UNAVAILABLE),
    SMTP_AUTHENTICATION_ERROR(2301, "SMTP authentication failed", HttpStatus.UNAUTHORIZED),
    SMTP_SENDING_ERROR(2302, "Error sending email via SMTP", HttpStatus.INTERNAL_SERVER_ERROR),

    // Lỗi AWS SES
    SES_CONNECTION_ERROR(2400, "Unable to connect to AWS SES", HttpStatus.SERVICE_UNAVAILABLE),
    SES_AUTHENTICATION_ERROR(2401, "AWS SES authentication failed", HttpStatus.UNAUTHORIZED),
    SES_SENDING_ERROR(2402, "Error sending email via AWS SES", HttpStatus.INTERNAL_SERVER_ERROR),

    // Lỗi tệp đính kèm
    ATTACHMENT_ERROR(2500, "Error processing email attachment", HttpStatus.BAD_REQUEST),
    ATTACHMENT_TOO_LARGE(2501, "Attachment size exceeds the allowed limit", HttpStatus.PAYLOAD_TOO_LARGE),

    // Lỗi giới hạn
    RATE_LIMIT_EXCEEDED(2600, "Email sending rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS);

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
