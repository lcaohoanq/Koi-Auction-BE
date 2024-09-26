package com.swp391.koibe.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KoiImageDTO {

    @JsonProperty("koi_id")
    @Min(value = 1, message = "Koi ID must be greater than 0")
    private Long koiId;

    @JsonProperty("image_url")
    @Size(min = 5, max = 300, message = "Image URL must be between 5 and 300 characters")
    private String imageUrl;

}
