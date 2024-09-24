package com.swp391.koibe.controllers;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("${api.prefix}/auction_kois")
@RestController
@RequiredArgsConstructor
public class AuctionKoiController {

    @PostMapping("/generateFakeAuctionKois")
    private void generateFakeAuctions() {
        Faker faker = new Faker();

        String[] statuses = {"PENDING", "ONGOING", "SOLD", "CANCELLED"};

    }
}
