package com.swp391.koibe.services.biddinghistory;

import com.swp391.koibe.dtos.BidDTO;
import com.swp391.koibe.dtos.auctionkoi.UpdateAuctionKoiDTO;
import com.swp391.koibe.exceptions.base.DataNotFoundException;
import com.swp391.koibe.models.*;
import com.swp391.koibe.repositories.BidHistoryRepository;
import com.swp391.koibe.responses.BidResponse;
import com.swp391.koibe.services.Biddable;
import com.swp391.koibe.services.auctionkoi.IAuctionKoiService;
import com.swp391.koibe.services.auctionparticipant.IAuctionParticipantService;
import com.swp391.koibe.services.token.ITokenService;
import com.swp391.koibe.services.user.IUserService;
import com.swp391.koibe.utils.DTOConverter;
import com.swp391.koibe.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BiddingHistoryService implements IBiddingHistoryService, Biddable {

    private final BidHistoryRepository bidHistoryRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final IAuctionKoiService auctionKoiService;

    private final IUserService userService;

    private final ITokenService tokenService;

    private final IAuctionParticipantService auctionParticipantService;


    @Override
    public Bid createBidHistory(Bid bid) throws DataNotFoundException {
        return bidHistoryRepository.save(bid);
    }

    @Override
    public Bid getBidHistoryById(long id) {
        return bidHistoryRepository.findById(id).orElse(null);
    }

    @Override
    public Page<BidResponse> getAllBidHistories(Pageable pageable) {
        return bidHistoryRepository.findAll(pageable).map(DTOConverter::convertToBidDTO);
    }

    @Override
    public Bid updateBidHistory(long id, Bid bid) {
        Bid bidHistory = getBidHistoryById(id);
        if (bidHistory == null) {
            return null;
        }
        bidHistory.setBidAmount(bid.getBidAmount());
        bidHistory.setBidTime(bid.getBidTime());
        bidHistory.setBidder(bid.getBidder());
        bidHistory.setAuctionKoi(bid.getAuctionKoi());
        return bidHistoryRepository.save(bidHistory);
    }

    @Override
    public void deleteBidHistory(long id) {
        bidHistoryRepository.deleteById(id);
    }

    @Override
    public boolean existsById(long id) {
        return bidHistoryRepository.existsById(id);
    }

    @Override
    public void createBidHistories(List<Bid> bidHistories) throws DataNotFoundException {
        for (Bid bid : bidHistories) {
            createBidHistory(bid);
        }
    }

    @Override
    public List<Bid> getBidsByAuctionKoiId(Long auctionKoiId) {
        return bidHistoryRepository.findAllByAuctionKoiId(auctionKoiId);
    }

    @Override
    public boolean hasBid(Long auctionKoiId, Long bidderId) {
        return bidHistoryRepository.existsByAuctionKoiIdAndBidderId(auctionKoiId, bidderId);
    }

    @Override
    public Bid getBidderLatestBid(Long auctionKoiId, Long bidderId) {
        return bidHistoryRepository.findAllByAuctionKoiId(auctionKoiId).stream()
                .filter(bid -> bid.getBidder().getId().equals(bidderId))
                .max(Comparator.comparing(Bid::getBidTime))
                .orElse(null);
    }

    @Override
    public AuctionKoi ascending(AuctionKoi auctionKoi, User bidder, Bid bid, Long latestBid) throws Exception {
        //8.1 with Ascending Bid (going from floor to ceiling) (ceiling price is not public)

        // 8.1.1 check the Bid_Amount and Current_Price + Bid_Step
        if (bid.getBidAmount() < auctionKoi.getCurrentBid() + auctionKoi.getBidStep()) {
            throw new IllegalArgumentException("Bid amount must be higher than the current bid plus bid step");
        }

        // 8.1.2 check Bidder Balance => calculate the cost if bidder already bid before
        // calculate bidder payment
        Long paymentAmount = bid.getBidAmount() - latestBid;

        // 8.1.4 update wallet balance if successful
        userService.updateUserBalance(bidder.getId(), paymentAmount);

        // 8.1.5 if Bid_amount is higher than ceiling price, set is_sold true
        if (bid.getBidAmount() >= auctionKoi.getCeilPrice()) {
            auctionKoi.setSold(true);
        }

        return auctionKoi;
    }

    @Override
    public AuctionKoi descending(AuctionKoi auctionKoi, User bidder, Bid bid) throws Exception {
        //8.4 with Bid Descending (going from ceiling price to floor price) (floor price is not public)

        //8.4.2 check Bidder Balance
        userService.updateUserBalance(bidder.getId(), bid.getBidAmount());

        //8.4.5 set is_sold true
        auctionKoi.setSold(true);
        return auctionKoi;
    }

    @Override
    public AuctionKoi fixedPrice(AuctionKoi auctionKoi, User bidder, Bid bid) throws Exception {
        //8.2 with Bid Fixed Price (floor price)

        //8.2.2 check Bidder Balance
        userService.updateUserBalance(bidder.getId(), bid.getBidAmount());

        //8.2.5 set is_sold true
        auctionKoi.setSold(true);
        return auctionKoi;
    }

    @Override
    public AuctionKoi sealed(AuctionKoi auctionKoi, User bidder, Bid bid) throws Exception {
        //8.3 with Bid Sealed (higher than the new floor price)

        //8.3.1 check if bidder has been placed before
        if (getBidderLatestBid(auctionKoi.getId(), bidder.getId()) != null) {
            throw new IllegalArgumentException("Bidder has already placed a bid");
        }

        //8.3.3 check Bidder Balance
        userService.updateUserBalance(bidder.getId(), bid.getBidAmount());
        return auctionKoi;
    }

    @Transactional
    @Override
    public BidResponse placeBid(BidDTO bidRequest) throws Exception {
        // 1. get the auction koi by id
        AuctionKoi auctionKoi = auctionKoiService.getAuctionKoiById(bidRequest.auctionKoiId());
        // 2. get the bidder by token
        User bidder = tokenService.findUserByToken(bidRequest.bidderToken()).getUser();
        // 3. get the auction by auction koi
        Auction auction = auctionKoi.getAuction();

        Bid bid = Bid.builder()
                .auctionKoi(auctionKoi)
                .bidder(bidder)
                .bidAmount(bidRequest.bidAmount())
                .bidTime(LocalDateTime.now())
                .build();

        validateBid(auction, auctionKoi, bid, bidder);

        // get bidder Latest Bid
        Bid latestBid = getBidderLatestBid(auctionKoi.getId(), bidder.getId());

        // 9. check the bid method and call the corresponding method
        auctionKoi = switch (auctionKoi.getBidMethod()) {
            case ASCENDING_BID -> ascending(auctionKoi, bidder, bid, latestBid != null ? latestBid.getBidAmount() : 0);
            case DESCENDING_BID -> descending(auctionKoi, bidder, bid);
            case FIXED_PRICE -> fixedPrice(auctionKoi, bidder, bid);
            case SEALED_BID -> sealed(auctionKoi, bidder, bid);
            default -> throw new IllegalArgumentException("Invalid bid method");
        };

        updateAuctionParticipant(auction, bidder, bid, latestBid);

        // Update the current bid and current bidder of the auction koi
        UpdateAuctionKoiDTO updateAuctionKoiDTO = UpdateAuctionKoiDTO.builder()
                .basePrice(auctionKoi.getBasePrice())
                .bidStep(auctionKoi.getBidStep())
                .bidMethod(String.valueOf(auctionKoi.getBidMethod()))
                .currentBid(bid.getBidAmount())
                .currentBidderId(bid.getBidder().getId())
                .isSold(auctionKoi.isSold())
                .build();

        bidHistoryRepository.save(bid);
        auctionKoiService.updateAuctionKoi(auctionKoi.getId(), updateAuctionKoiDTO);

        BidResponse bidResponse = BidResponse.builder()
                .auctionKoiId(auctionKoi.getId())
                .bidderId(bidder.getId())
                .bidAmount(bidRequest.bidAmount())
                .bidderName(bidder.getFirstName() + " " + bidder.getLastName())
                .bidTime(bid.getBidTime().toString())
                .build();

        // Send the update to the specific auction koi topic
        messagingTemplate.convertAndSend("/topic/auctionkoi/" + auctionKoi.getId(), bidResponse);

        return bidResponse;
    }

    private void validateBid(Auction auction, AuctionKoi auctionKoi, Bid bid, User bidder) {
        // 4. check if the auction is active
        if (!DateTimeUtils.isAuctionActive(auction.getStartTime(), auction.getEndTime(), bid.getBidTime())) {
            throw new IllegalArgumentException("AuctionKoi has been ended!");
        }

        // 5. check if koi in auction is sold or not
        if (auctionKoi.isSold()) {
            throw new IllegalArgumentException("AuctionKoi has been sold!");
        }

        // 6. because the current bid can be null, so we need to check if the current bid is null
        if (auctionKoi.getCurrentBid() == null) {
            auctionKoi.setCurrentBid(0L);
        }

        // 7. Check if the bidder is the owner of the koi
        if (bidder.getId().equals(auctionKoi.getKoi().getOwner().getId()) ) {
            throw new IllegalArgumentException("Owner can not Bid your Koi");
        }

        // 8. Check if the bid amount is higher than the base price
        if (bid.getBidAmount() < auctionKoi.getBasePrice()) {
            throw new IllegalArgumentException("Bid amount must be higher than the base price");
        }
    }

    private void updateAuctionParticipant(Auction auction, User bidder, Bid bid, Bid latestBid) {
        // 10. update Auction Participant table if it is the first time bidder
        if (latestBid == null) {
            // check if the bidder has already joined the auction, if not, create a new record
            if (!auctionParticipantService.hasJoinedAuction(auction.getId(), bidder.getId())) {
                AuctionParticipant auctionParticipant = AuctionParticipant.builder()
                        .auction(auction)
                        .user(bidder)
                        .joinTime(bid.getBidTime())
                        .build();
                auctionParticipantService.createAuctionParticipant(auctionParticipant);
            }
        }
    }
}
