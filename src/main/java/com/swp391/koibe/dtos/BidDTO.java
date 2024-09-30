package com.swp391.koibe.dtos;

import lombok.Builder;

@Builder
public record BidDTO (
    Long auctionKoiId,
    Long bidderId,
    int bidAmount
) {}