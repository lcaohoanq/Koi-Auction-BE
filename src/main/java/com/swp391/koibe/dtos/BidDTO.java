package com.swp391.koibe.dtos;

import lombok.Data;

@Data
public class BidDTO {
    private Long auctionKoiId;
    private Long bidderId;
    private int bidAmount;
}