package vn.hvt.SpringMailPro.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import vn.hvt.SpringMailPro.dto.ApiResponse;
import vn.hvt.SpringMailPro.dto.EmailResponse;
import vn.hvt.SpringMailPro.dto.EmailRequest;
import vn.hvt.SpringMailPro.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ApiResponse<EmailResponse> sendEmail(
            @Valid @RequestBody EmailRequest request) {
        EmailResponse response = emailService.sendEmail(request);


        return ApiResponse.<EmailResponse>builder()
                .data(response)
                .build();
    }


    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<EmailResponse>>> sendBulkEmails(
            @Valid @RequestBody List<EmailRequest> requests,
            @RequestParam(defaultValue = "true") boolean useQueue) {

        List<EmailResponse> responses = emailService.sendBulkEmails(requests, useQueue);
        return ResponseEntity.ok(ApiResponse.<List<EmailResponse>>builder()
                .data(responses)
                .build());
    }

}