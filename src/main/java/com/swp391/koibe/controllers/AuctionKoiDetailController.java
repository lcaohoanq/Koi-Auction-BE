package com.swp391.koibe.controllers;

import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.AuctionKoi;
import com.swp391.koibe.models.AuctionParticipant;
import com.swp391.koibe.models.BidHistory;
import com.swp391.koibe.models.User;
import com.swp391.koibe.repositories.AuctionParticipantRepository;
import com.swp391.koibe.services.auctionkoi.IAuctionKoiService;
import com.swp391.koibe.services.auctionkoidetail.IAuctionKoiDetailService;
import com.swp391.koibe.services.user.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/auctionkoidetails")
@RequiredArgsConstructor
public class AuctionKoiDetailController {

    private final IAuctionKoiService auctionKoiService;
    private final IUserService userService;
    private final IAuctionKoiDetailService auctionKoiDetailService;
    private final AuctionParticipantRepository auctionParticipantRepository;

    @PostMapping("/generateFakeAuctionKoiDetails")
    public ResponseEntity<String> generateFakeAuctionKoiDetails() {
        try {
            List<AuctionKoi> auctionKois = auctionKoiService.getAuctionIsSold();
            List<User> users = userService.getAllUsers();
            Random random = new Random();

            for (AuctionKoi auctionKoi : auctionKois) {
                List<BidHistory> bidHistories = new ArrayList<>();
                Set<Long> participantIds = new HashSet<>();
                int numberOfBids = random.nextInt(5, 16); // Random number of bids between 5 and 15
                float currentBidAmount = auctionKoi.getBasePrice();

                for (int i = 1; i <= numberOfBids; i++) {
                    User bidder = users.get(random.nextInt(users.size()));
                    BidHistory bidHistory = new BidHistory();
                    bidHistory.setAuctionKoi(auctionKoi);
                    bidHistory.setBidder(bidder);

                    // Ensure each bid is higher than the previous one
                    currentBidAmount += auctionKoi.getBidStep() * (1 + random.nextFloat());
                    bidHistory.setBidAmount(Math.round(currentBidAmount));

                    // Randomize bid time within auction duration
                    LocalDateTime startTime = auctionKoi.getAuction().getStartTime();
                    LocalDateTime endTime = auctionKoi.getAuction().getEndTime();
                    long secondsBetween = startTime.until(endTime, ChronoUnit.SECONDS);
                    long randomSeconds = random.nextLong(secondsBetween);
                    bidHistory.setBidTime(startTime.plusSeconds(randomSeconds));

                    bidHistories.add(bidHistory);

                    // Add participant if not already added
                    if (!participantIds.contains(bidder.getId())) {
                        AuctionParticipant participant = new AuctionParticipant();
                        participant.setAuction(auctionKoi.getAuction());
                        participant.setUser(bidder);
                        participant.setJoinTime(bidHistory.getBidTime());
                        auctionParticipantRepository.save(participant);
                        participantIds.add(bidder.getId());
                    }
                }

                // Sort bid histories by time
                bidHistories.sort(Comparator.comparing(BidHistory::getBidTime));

                // Set the last bid as the winning bid
                BidHistory winningBid = bidHistories.get(bidHistories.size() - 1);
                auctionKoi.setCurrentBid(winningBid.getBidAmount());
                auctionKoi.setCurrentBidderId(winningBid.getBidder().getId());

                // Save all bid histories in batch
                auctionKoiDetailService.createBidHistories(bidHistories);

                // Update the AuctionKoi
                auctionKoiService.updateAuctionKoi(auctionKoi.getId(), auctionKoi);
            }

            return ResponseEntity.ok("Fake auction koi details and participants generated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating fake auction koi details: " + e.getMessage());
        }
    }

}
