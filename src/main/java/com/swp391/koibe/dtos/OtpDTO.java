package com.swp391.koibe.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OtpDTO {

    @JsonProperty("email")
    private String email;

    @JsonProperty("otp")
    private String otp;

    @JsonProperty("expired_at")
    private LocalDateTime expiredAt; // in milliseconds

    @JsonProperty("is_used")
    private boolean isUsed;

    @JsonProperty("is_expired")
    private boolean isExpired;

}
