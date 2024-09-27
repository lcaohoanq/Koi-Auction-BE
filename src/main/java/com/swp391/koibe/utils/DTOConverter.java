package com.swp391.koibe.utils;

import com.swp391.koibe.models.Auction;
import com.swp391.koibe.models.AuctionKoi;
import com.swp391.koibe.models.Koi;
import com.swp391.koibe.models.User;
import com.swp391.koibe.responses.AuctionKoiResponse;
import com.swp391.koibe.responses.AuctionResponse;
import com.swp391.koibe.responses.KoiResponse;
import com.swp391.koibe.responses.UserPaginationResponse;
import com.swp391.koibe.responses.UserResponse;

public class DTOConverter {

    public static UserResponse convertToUserDTO(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phoneNumber(user.getPhoneNumber())
            .email(user.getEmail())
            .address(user.getAddress())
            //.password(user.getPassword())
            .statusName(user.getStatus() != null ? user.getStatus().getStatus() : null)
            .dob(user.getDob())
            .avatarUrl(user.getAvatarUrl())
            .googleAccountId(user.getGoogleAccountId())
            .roleName(user.getRole() != null ? user.getRole().getName() : null)
            .accountBalance(user.getAccountBalance())
            .build();
    }

    public static KoiResponse convertToKoiDTO(Koi koi) {
        return KoiResponse.builder()
            .id(koi.getId())
            .name(koi.getName())
            .sex(koi.getSex())
            .length(koi.getLength())
            .age(koi.getAge())
            //.basePrice(koi.getBasePrice())
            .statusName(koi.getStatus() != null ? koi.getStatus().getStatus() : null)
            .idDisplay(koi.getIsDisplay())
            .thumbnail(koi.getThumbnail())
            .description(koi.getDescription())
            .ownerId(koi.getOwner().getId())
            .categoryId(koi.getCategory().getId())
            .build();
    }

    public static AuctionResponse convertToAuctionDTO(Auction auction) {
        return AuctionResponse.builder()
            .id(auction.getId())
            .title(auction.getTitle())
            .startTime(auction.getStartTime())
            .endTime(auction.getEndTime())
            .status(auction.getStatus() != null ? auction.getStatus().getStatus() : null)
            .build();
    }

    public static AuctionKoiResponse convertToAuctionKoiDTO(AuctionKoi auctionKoi) {
        return AuctionKoiResponse.builder()
            .id(auctionKoi.getId())
            .auctionId(auctionKoi.getAuction().getId())
            .koiId(auctionKoi.getKoi().getId())
            .basePrice(auctionKoi.getBasePrice())
            .bidMethod(auctionKoi.getBidMethod() != null ? auctionKoi.getBidMethod().getType() : null)
            .currentBid(auctionKoi.getCurrentBid())
            .currentBidderId(auctionKoi.getCurrentBidderId())
            .isSold(auctionKoi.isSold())
            .build();
    }

}
