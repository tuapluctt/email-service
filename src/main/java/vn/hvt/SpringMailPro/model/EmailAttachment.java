package vn.hvt.SpringMailPro.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailAttachment implements Serializable {
    private String filename;
    private byte[] content;
    private String contentType;
}
