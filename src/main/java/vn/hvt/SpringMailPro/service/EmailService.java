package vn.hvt.SpringMailPro.service;

import vn.hvt.SpringMailPro.dto.EmailRequest;
import vn.hvt.SpringMailPro.dto.EmailResponse;
import vn.hvt.SpringMailPro.model.Email;

import java.util.List;
import java.util.Map;

public interface EmailService {
    EmailResponse sendEmail(EmailRequest emailRequest);

    List<EmailResponse> sendBulkEmails(List<EmailRequest>  emailRequest, boolean useQueue);

    EmailResponse queueEmail(Email email);
}
