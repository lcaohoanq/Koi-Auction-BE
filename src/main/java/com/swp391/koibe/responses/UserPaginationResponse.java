package com.swp391.koibe.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swp391.koibe.responses.base.BasePaginationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPaginationResponse extends BasePaginationResponse {

    @JsonProperty("item")
    private List<UserResponse> items; // List of users
}
