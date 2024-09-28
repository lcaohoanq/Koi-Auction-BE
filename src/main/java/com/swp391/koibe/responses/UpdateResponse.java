package com.swp391.koibe.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swp391.koibe.responses.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateResponse extends BaseResponse {

    private UserResponse userResponse;

}
