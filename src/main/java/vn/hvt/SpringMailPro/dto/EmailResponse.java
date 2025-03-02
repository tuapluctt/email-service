package vn.hvt.SpringMailPro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponse {
    private boolean success;
    private String messageId;
    private String error;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static EmailResponse success(String messageId, String provider) {
        return EmailResponse.builder()
                .success(true)
                .messageId(messageId)
                .build();
    }

    public static EmailResponse failure(String error) {
        return EmailResponse.builder()
                .success(false)
                .error(error)
                .build();
    }
}