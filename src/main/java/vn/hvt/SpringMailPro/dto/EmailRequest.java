package vn.hvt.SpringMailPro.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vn.hvt.SpringMailPro.model.EmailAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class EmailRequest {
    @NotEmpty(message = "Recipient email(s) must not be empty")
    private String[] to;
    private String[] cc;
    private String[] bcc;

    @Email(message = "From email must be valid")
    private String from;

    @NotNull(message = "Subject must not be null")
    private String subject;

    private String text;
    private String html;
    private String replyTo;
    private List<EmailAttachment> attachments = new ArrayList<>();

    // For template emails
    private String templateName;
    private Map<String, Object> templateModel;
}