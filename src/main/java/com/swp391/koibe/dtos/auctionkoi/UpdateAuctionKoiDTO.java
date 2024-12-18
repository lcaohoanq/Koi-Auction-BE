package com.swp391.koibe.dtos.auctionkoi;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record UpdateAuctionKoiDTO (
    @JsonProperty("base_price")
    @NotNull(message = "Base rice is required")
    @Positive(message = "Base price must be positive")
    @Min(value = 10000, message = "Minimum koi price is 10000")
    @Max(value = 200000000, message = "Maximum koi price is 200 millions")
    long basePrice,

    @JsonProperty("bid_step")
    @NotNull(message = "Bid Step is required")
    @Positive(message = "Bid step must be positive")
    @Min(value = 50000, message = "Minimum bid step is 50000")
    long bidStep,

    @JsonProperty("ceil_price")
    Long ceilPrice, //this field is optional, can be null but will set later, if the auction is DESCENDING_BID

    @JsonProperty("bid_method")
    @NotBlank(message = "Bid Method is required")
    @Pattern(regexp = "FIXED_PRICE|SEALED_BID|ASCENDING_BID|DESCENDING_BID", message = "Bid Method must be either FIXED_PRICE, SEALED_BID, ASCENDING_BID, DESCENDING_BID")
    String bidMethod,

    //this field is optional, can be null but will set later, if the auction is ONGOING
    @Positive(message = "Current bid must be positive")
    @JsonProperty("current_bid") Long currentBid,
    @Positive(message = "Current bidder ID must be positive")
    @JsonProperty("current_bidder_id") Long currentBidderId,

    @JsonProperty("is_sold") boolean isSold,

    @JsonProperty("revoked") int revoked
) {}
