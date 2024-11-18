package com.swp391.koibe.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swp391.koibe.models.User;
import com.swp391.koibe.responses.base.BaseResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@JsonInclude(Include.NON_NULL)
public class RegisterResponse extends BaseResponse<UserResponse> { }