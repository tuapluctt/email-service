### Công nghệ sử dụng
*   **Ngôn ngữ:** Java 
*   **Framework:** Spring Boot (Phiên bản 3.x)
*   **Email Core:** Spring Boot Starter Mail (`JavaMailSender`)
*   **AWS SDK:** AWS SDK for Java v2 (cho tích hợp SES)
*   **Template Engine :** Thymeleaf 
*   **Build Tool:** Maven

### Tính năng

*   Gửi email văn bản thuần túy (plain text).
*   Gửi email định dạng HTML.
*   Gửi email sử dụng template engine (ví dụ: Thymeleaf).
*   Gửi email với một hoặc nhiều file đính kèm.
*   Gửi email qua SMTP.
*   Gửi email qua AWS SES.


### Gửi email đơn lẻ

Gửi yêu cầu POST đến `/api/emails/send` với payload:
```json
{
    "to": ["hoanganhtu66hat@gmail.com"],
    "subject": "Thử nghiệm email",
    "text": "Đây là nội dung email thử nghiệm."
}
```

### Gửi email với tệp đính kèm

Gửi yêu cầu POST đến `/api/emails/send` với payload:
```json
{
  "to": ["hoanganhtu66hat@gmail.com"],
  "subject": "Email với Tệp Đính Kèm",
  "text": "Đây là email có kèm theo tệp đính kèm.",
  "attachments": [
    {
      "filename": "SendEmailAttachment.txt",
      "content": "dGVzdCBzZW5kIGVtYWkgd2lodCBhdHRhY2htZW50", 
      "contentType": "application/txt"
    }
  ]
}
```

### Gửi email với template

Gửi yêu cầu POST đến `/api/emails/send` với payload:
```json
{
  "to": ["hoanganhtu66hat@gmail.com"],
  "cc": ["hoangvantu2@dtu.edu.vn"],
  "subject": "Invoice #INV-2025-0324",
  "templateName": "invoice",
  "templateModel": {
    "invoiceNumber": "INV-2025-0324",
    "customerName": "Nguyễn Văn A",
    "items": [
      {
        "name": "Dịch vụ hosting website",
        "quantity": 1,
        "price": "2.400.000đ",
        "total": "2.400.000đ"
      },
      {
        "name": "SSL Certificate (1 năm)",
        "quantity": 1,
        "price": "750.000đ",
        "total": "750.000đ"
      },
      {
        "name": "Dịch vụ backup dữ liệu",
        "quantity": 2,
        "price": "350.000đ",
        "total": "700.000đ"
      }
    ],
    "total": "3.850.000đ",
    "paymentUrl": "https://yourcompany.com/payment?invoice=INV-2025-0324&token=eyJhbGciOiJIUzI1NiJ9"
  }
}
```
