package com.swp391.koibe.controllers;

import com.github.javafaker.Faker;
import com.swp391.koibe.enums.EAuctionStatus;
import com.swp391.koibe.models.Auction;
import com.swp391.koibe.responses.AuctionResponse;
import com.swp391.koibe.services.auction.IAuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequestMapping("${api.prefix}/auctions")
@RestController
@RequiredArgsConstructor
public class AuctionController {

    private final IAuctionService auctionService;

    @PostMapping("/generateFakeAuctions")
    private void generateFakeAuctions() {
        Faker faker = new Faker();

        for (int i = 0; i < 100; i++) {
            LocalDateTime endTime = LocalDateTime.ofInstant(faker.date().past(30, TimeUnit.DAYS).toInstant(), ZoneId.systemDefault());
            LocalDateTime startTime = endTime.minusDays(faker.number().numberBetween(1, 20));

            Auction auction = Auction.builder()
                    .status(EAuctionStatus.ENDED)
                    .endTime(endTime)
                    .startTime(startTime)
                    .title(" Auction #" + i)
                    .build();
            try {
                 auctionService.createAuction(auction);
            } catch (Exception e) {
                log.error("Error creating auction: " + e.getMessage());
            }
        }
    }

    //pagination for auctions
    @GetMapping("") // /auctions/?page=1&limit=10
    public ResponseEntity<List<AuctionResponse>> getAllAuctions(
            @RequestParam int page,
            @RequestParam int limit) {
        try {
            PageRequest pageRequest = PageRequest.of(page, limit);
            Page<AuctionResponse> auctions = auctionService.getAllAuctions(pageRequest);
            return ResponseEntity.ok(auctions.getContent());
        }
        catch (Exception e) {
            log.error("Error getting all auctions: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
}
