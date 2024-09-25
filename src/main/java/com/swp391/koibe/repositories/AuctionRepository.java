package com.swp391.koibe.repositories;

import com.swp391.koibe.models.Auction;
import com.swp391.koibe.responses.AuctionResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<AuctionResponse> findAllByStatus(String status);

}
