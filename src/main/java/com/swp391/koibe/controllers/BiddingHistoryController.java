package com.swp391.koibe.controllers;

import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.*;
import com.swp391.koibe.repositories.AuctionParticipantRepository;
import com.swp391.koibe.services.auctionkoi.IAuctionKoiService;
import com.swp391.koibe.services.biddinghistory.IBiddingHistoryService;
import com.swp391.koibe.services.user.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/auctionkoidetails")
@RequiredArgsConstructor
public class BiddingHistoryController {

    private final IAuctionKoiService auctionKoiService;
    private final IUserService userService;
    private final IBiddingHistoryService biddingHistoryService;
    private final AuctionParticipantRepository auctionParticipantRepository;

    @PostMapping("/generateFakeAuctionKoiDetails")
    public ResponseEntity<String> generateFakeAuctionKoiDetails() {
        try {
            List<AuctionKoi> auctionKois = auctionKoiService.getAuctionIsSold();
            List<User> users = userService.getAllUsers();
            Random random = new Random();

            // Group AuctionKois by Auction
            Map<Auction, List<AuctionKoi>> auctionKoisByAuction = auctionKois.stream()
                    .collect(Collectors.groupingBy(AuctionKoi::getAuction));

            for (Map.Entry<Auction, List<AuctionKoi>> entry : auctionKoisByAuction.entrySet()) {
                Auction auction = entry.getKey();
                List<AuctionKoi> auctionKoisForAuction = entry.getValue();
                Set<Long> participantIds = new HashSet<>();

                for (AuctionKoi auctionKoi : auctionKoisForAuction) {
                    List<BidHistory> bidHistories = new ArrayList<>();
                    int numberOfBids = random.nextInt(5, 16); // Random number of bids between 5 and 15
                    int currentBidAmount = auctionKoi.getBasePrice().intValue();

                    for (int i = 1; i <= numberOfBids; i++) {
                        long ownerId = auctionKoi.getKoi().getOwner().getId();
                        User bidder = users.stream()
                                .filter(user -> user.getId() != ownerId)
                                .skip(random.nextInt(users.size() - 1))
                                .findFirst()
                                .orElseThrow(() -> new DataNotFoundException("No suitable bidder found"));

                        BidHistory bidHistory = new BidHistory();
                        bidHistory.setAuctionKoi(auctionKoi);
                        bidHistory.setBidder(bidder);

                        // Ensure each bid is higher than the previous one
                        currentBidAmount += auctionKoi.getBidStep();
                        bidHistory.setBidAmount(Math.round(currentBidAmount));

                        LocalDateTime bidTime = auctionKoi.getAuction().getStartTime().plusHours(i);
                        bidHistory.setBidTime(bidTime);

                        bidHistories.add(bidHistory);

                        // Add participant if not already added for this auction
                        if (!participantIds.contains(bidder.getId())) {
                            AuctionParticipant participant = new AuctionParticipant();
                            participant.setAuction(auction);
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
                    biddingHistoryService.createBidHistories(bidHistories);

                    // Update the AuctionKoi
                    auctionKoiService.updateAuctionKoi(auctionKoi.getId(), auctionKoi);
                }
            }

            return ResponseEntity.ok("Fake auction koi details and participants generated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating fake auction koi details: " + e.getMessage());
        }
    }

}
