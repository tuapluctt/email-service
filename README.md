### Gửi email đơn lẻ

Gửi yêu cầu POST đến `/api/emails/send` với payload:
```json
{
    "to": ["hoanganhtu66hat@gmailcom"],
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
