package com.swp391.koibe.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swp391.koibe.responses.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtpResponse extends BaseResponse {

}
