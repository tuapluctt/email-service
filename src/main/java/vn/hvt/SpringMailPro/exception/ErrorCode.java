package vn.hvt.SpringMailPro.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    // Lỗi chung
    EMAIL_GENERAL_ERROR(2000, "Lỗi không xác định khi gửi email", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_EMAIL_REQUEST(2001, "Yêu cầu email không hợp lệ", HttpStatus.BAD_REQUEST),

    // Lỗi cấu hình
    EMAIL_CONFIG_ERROR(2100, "Lỗi cấu hình email service", HttpStatus.SERVICE_UNAVAILABLE),
    PROVIDER_NOT_AVAILABLE(2101, "No email providers are available", HttpStatus.SERVICE_UNAVAILABLE),

    // Lỗi template
    TEMPLATE_ERROR(2200, "Error processing template email", HttpStatus.INTERNAL_SERVER_ERROR),
    TEMPLATE_NOT_FOUND(2201, "Không tìm thấy template", HttpStatus.NOT_FOUND),

    // Lỗi SMTP
    SMTP_CONNECTION_ERROR(2300, "Không thể kết nối đến máy chủ SMTP", HttpStatus.SERVICE_UNAVAILABLE),
    SMTP_AUTHENTICATION_ERROR(2301, "Xác thực SMTP thất bại", HttpStatus.UNAUTHORIZED),
    SMTP_SENDING_ERROR(2302, "Lỗi khi gửi email qua SMTP", HttpStatus.INTERNAL_SERVER_ERROR),

    // Lỗi AWS SES
    SES_CONNECTION_ERROR(2400, "Không thể kết nối đến AWS SES", HttpStatus.SERVICE_UNAVAILABLE),
    SES_AUTHENTICATION_ERROR(2401, "Xác thực AWS SES thất bại", HttpStatus.UNAUTHORIZED),
    SES_SENDING_ERROR(2402, "Lỗi khi gửi email qua AWS SES", HttpStatus.INTERNAL_SERVER_ERROR),

    // Lỗi tệp đính kèm
    ATTACHMENT_ERROR(2500, "Lỗi xử lý tệp đính kèm", HttpStatus.BAD_REQUEST),
    ATTACHMENT_TOO_LARGE(2501, "Tệp đính kèm vượt quá kích thước cho phép", HttpStatus.PAYLOAD_TOO_LARGE),

    // Lỗi giới hạn
    RATE_LIMIT_EXCEEDED(2600, "Đã vượt quá giới hạn gửi email", HttpStatus.TOO_MANY_REQUESTS);

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
