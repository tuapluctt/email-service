package vn.hvt.SpringMailPro.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiRespponse<T> {
    @Builder.Default
    int code = 1000;
    String message ;
    T data;
}
