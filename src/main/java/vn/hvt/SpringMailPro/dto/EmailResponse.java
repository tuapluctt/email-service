package vn.hvt.SpringMailPro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponse {
    private boolean success;
    private String messageId;
    private String error;

    @Builder.Default
    private List<String> invalidAddresses = new ArrayList<>();

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