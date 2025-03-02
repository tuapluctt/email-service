package vn.hvt.SpringMailPro.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Email implements Serializable {
    private String[] to;
    private String[] cc;
    private String[] bcc;
    private String from;
    private String subject;
    private String text;
    private String html;
    private String replyTo;

    @Builder.Default
    private List<EmailAttachment> attachments = new ArrayList<>();

    // Template related fields
    private String templateName;
    private Object templateModel;
}
