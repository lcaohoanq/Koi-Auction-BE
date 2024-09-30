package com.swp391.koibe.responses;

import com.swp391.koibe.responses.base.BaseResponse;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Setter
public class UpdateResponse extends BaseResponse {

    private UserResponse userResponse;

}
