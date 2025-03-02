package vn.hvt.SpringMailPro.service;

import vn.hvt.SpringMailPro.dto.EmailResponse;
import vn.hvt.SpringMailPro.model.Email;

import java.util.List;
import java.util.Map;

public interface EmailService {
    EmailResponse sendEmail(Email email);

    EmailResponse sendTemplateEmail(String[] to, String templateName, Map<String, Object> model, String subject);

    List<EmailResponse> sendBulkEmails(List<Email> emails, boolean useQueue);

    EmailResponse queueEmail(Email email);
}
