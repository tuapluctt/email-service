package vn.hvt.SpringMailPro.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import vn.hvt.SpringMailPro.dto.ApiRespponse;
import vn.hvt.SpringMailPro.dto.EmailResponse;
import vn.hvt.SpringMailPro.dto.EmailRequest;
import vn.hvt.SpringMailPro.model.Email;
import vn.hvt.SpringMailPro.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<ApiRespponse<EmailResponse>> sendEmail(@Valid @RequestBody EmailRequest request) {
        Email email = mapRequestToEmail(request);
        EmailResponse response = emailService.sendEmail(email);
        return ResponseEntity.ok(ApiRespponse.<EmailResponse>builder()
                        .data(response)
                        .build());
    }

    @PostMapping("/template")
    public ResponseEntity<ApiRespponse<EmailResponse>> sendTemplateEmail(@Valid @RequestBody EmailRequest request) {
        EmailResponse response = emailService.sendTemplateEmail(
                request.getTo(),
                request.getTemplateName(),
                request.getTemplateModel(),
                request.getSubject()
        );

        return ResponseEntity.ok(ApiRespponse.<EmailResponse>builder()
                .data(response)
                .build());
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiRespponse<List<EmailResponse>>> sendBulkEmails(
            @Valid @RequestBody List<EmailRequest> requests,
            @RequestParam(defaultValue = "true") boolean useQueue) {

        List<Email> emails = requests.stream()
                .map(this::mapRequestToEmail)
                .collect(Collectors.toList());

        List<EmailResponse> responses = emailService.sendBulkEmails(emails, useQueue);
        return ResponseEntity.ok(ApiRespponse.<List<EmailResponse>>builder()
                .data(responses)
                .build());
    }

    @PostMapping("/queue")
    public ResponseEntity<ApiRespponse<EmailResponse>> queueEmail(@Valid @RequestBody EmailRequest request) {
        Email email = mapRequestToEmail(request);
        EmailResponse response = emailService.queueEmail(email);
        return ResponseEntity.ok(ApiRespponse.<EmailResponse>builder()
                .data(response)
                .build());
    }


    private Email mapRequestToEmail(EmailRequest request) {
        return Email.builder()
                .to(request.getTo())
                .cc(request.getCc())
                .bcc(request.getBcc())
                .from(request.getFrom())
                .subject(request.getSubject())
                .text(request.getText())
                .html(request.getHtml())
                .replyTo(request.getReplyTo())
                .attachments(request.getAttachments())
                .templateName(request.getTemplateName())
                .templateModel(request.getTemplateModel())
                .build();
    }
}