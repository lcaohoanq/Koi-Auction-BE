package com.swp391.koibe.services.auctionkoi;

import com.swp391.koibe.dtos.auctionkoi.AuctionKoiDTO;
import com.swp391.koibe.dtos.auctionkoi.UpdateAuctionKoiDTO;
import com.swp391.koibe.enums.EAuctionStatus;
import com.swp391.koibe.enums.EBidMethod;
import com.swp391.koibe.enums.EmailCategoriesEnum;
import com.swp391.koibe.exceptions.MalformBehaviourException;
import com.swp391.koibe.exceptions.MalformDataException;
import com.swp391.koibe.exceptions.base.DataNotFoundException;
import com.swp391.koibe.models.Auction;
import com.swp391.koibe.models.AuctionKoi;
import com.swp391.koibe.models.Koi;
import com.swp391.koibe.models.User;
import com.swp391.koibe.repositories.AuctionKoiRepository;
import com.swp391.koibe.repositories.AuctionRepository;
import com.swp391.koibe.repositories.KoiRepository;
import com.swp391.koibe.responses.AuctionKoiResponse;
import com.swp391.koibe.responses.AuctionResponse;
import com.swp391.koibe.responses.BidMethodQuantityResponse;
import com.swp391.koibe.responses.KoiInAuctionResponse;
import com.swp391.koibe.services.auction.IAuctionService;
import com.swp391.koibe.services.mail.IMailService;
import com.swp391.koibe.utils.DTOConverter;
import jakarta.mail.MessagingException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class AuctionKoiService implements IAuctionKoiService {

    private final AuctionKoiRepository auctionKoiRepository;
    private final AuctionRepository auctionRepository;
    private final KoiRepository koiRepository;
    private final IAuctionService auctionService;
    private final IMailService mailService;

    @Override
    public AuctionKoi createAuctionKoi(AuctionKoiDTO auctionKoiDTO)
            throws DataNotFoundException, MessagingException {

        // check if the koi is already in another upcoming auction
        Set<Auction> currentAuctionList = auctionRepository.findAuctionsByStatus(EAuctionStatus.UPCOMING);
        currentAuctionList.addAll(auctionRepository.findAuctionsByStatus(EAuctionStatus.ONGOING));
        for (Auction auction : currentAuctionList) {
            List<AuctionKoi> auctionKoiList = auctionKoiRepository.findAuctionKoiByAuctionId(auction.getId());
            for (AuctionKoi auctionKoi : auctionKoiList) {
                if (Objects.equals(auctionKoi.getKoi().getId(), auctionKoiDTO.koiId())) {
                    throw new MalformDataException("This koi is already in another auction");
                }
            }
        }

        // check if the auction is already include this koi
        List<AuctionKoi> auctionKois = auctionKoiRepository.findAuctionKoiByAuctionId(
                auctionKoiDTO.auctionId());
        for (AuctionKoi auctionKoi : auctionKois) {
            if (Objects.equals(auctionKoi.getKoi().getId(), auctionKoiDTO.koiId())) {
                throw new MalformDataException("This koi is already in this auction");
            }
            // this will ensure that if the koi are in auction (already defined it) then
            // it will not be added to the auction again
            // prevent duplicate koi in auction and different bid method on the same koi in
            // the same auction
        }

        // check if auction exists
        auctionRepository.findById(auctionKoiDTO.auctionId())
                .orElseThrow(() -> new DataNotFoundException("Auction not found"));

        // check auction status is not ended
        AuctionResponse auction = auctionService.findAuctionById(auctionKoiDTO.auctionId());
        if (auction.status() == EAuctionStatus.ENDED || auction.status() == EAuctionStatus.ONGOING) {
            throw new MalformDataException("Auction are not allow to register");
        }

        // check if koi exists
        Optional<Koi> existingKoi = koiRepository.findById(auctionKoiDTO.koiId());
        if (existingKoi.isEmpty()) {
            throw new DataNotFoundException("Koi not found");
        }

        // Validate ceil price based on bid method
        Long validCeilPrice = null;
        if (EBidMethod.valueOf(auctionKoiDTO.bidMethod()) == EBidMethod.DESCENDING_BID ||
                EBidMethod.valueOf(auctionKoiDTO.bidMethod()) == EBidMethod.ASCENDING_BID) {
            if (auctionKoiDTO.ceilPrice() == null) {
                throw new MalformDataException("Ceil price is required for DESCENDING_BID and ASCENDING_BID");
            }
            validCeilPrice = auctionKoiDTO.ceilPrice(); // Set the provided ceil price

            if (validCeilPrice <= auctionKoiDTO.basePrice()) {
                throw new MalformDataException("Ceil price must be greater than base price");
            }

        }

        Long currentBidValue = auctionKoiDTO.currentBid() != null ? auctionKoiDTO.currentBid() : 0L;

        if (auctionKoiDTO.bidMethod().equals("DESCENDING_BID")) {
            currentBidValue = validCeilPrice;
        }

        AuctionResponse validAuction = auctionService.findAuctionById(auctionKoiDTO.auctionId());

        // save auction koi
        AuctionKoi newAuctionKoi = AuctionKoi.builder()
                .basePrice(auctionKoiDTO.basePrice())
                .bidStep(auctionKoiDTO.bidStep())
                .bidMethod(EBidMethod.valueOf(auctionKoiDTO.bidMethod()))
                .ceilPrice(validCeilPrice)
                .isSold(false) // default is false when add new koi to auction (if sold, update later)
                .currentBid(currentBidValue)
                .currentBidderId(
                        auctionKoiDTO.currentBidderId() != null ? auctionKoiDTO.currentBidderId() : 0L)
                .revoked(0) // default is 0 when add new koi to auction
                .auction(
                        Auction.builder().id(validAuction.id())
                            .title(validAuction.title())
                            .startTime(validAuction.startTime())
                            .endTime(validAuction.endTime())
                            .status(validAuction.status())
                            .auctioneer(User.builder().id(validAuction.auctioneerId()).build())
                            .build()
                )
                .koi(existingKoi.get())
                .build();

        User owner = existingKoi.get().getOwner();

        Context context = new Context();
        context.setVariable("name", owner.getFirstName());
        context.setVariable("koiName", existingKoi.get().getName());

        mailService.sendMail(
                owner.getEmail(),
                "Your koi has been added to an auction",
                EmailCategoriesEnum.KOI_ADDED_TO_AUCTION.getType(),
                context);

        return auctionKoiRepository.save(newAuctionKoi);
    }

    @Override
    public AuctionKoi getAuctionKoiById(long id) throws DataNotFoundException {
        return auctionKoiRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Auction Koi not found"));
    }

    @Override
    public List<AuctionKoi> getAuctionKoiByAuctionIdV2(long id) {
        return auctionKoiRepository.findAuctionKoiByAuctionId(id);
    }

    @Override
    public void updateDescendAuctionKoiPrice(long auctionKoiId, AuctionKoi auctionKoi) {
        AuctionKoi auctionKoiToUpdate = auctionKoiRepository.findById(auctionKoiId)
                .orElseThrow(() -> new DataNotFoundException("Auction Koi not found"));

        if (auctionKoiToUpdate.getBidMethod().equals(EBidMethod.DESCENDING_BID)) {
            if (auctionKoiToUpdate.getCurrentBid() > auctionKoiToUpdate.getBasePrice()) {
                auctionKoiToUpdate.setCurrentBid(
                        auctionKoiToUpdate.getCurrentBid() - auctionKoiToUpdate.getBidStep());
            } else {
                auctionKoiToUpdate.setCurrentBid(auctionKoiToUpdate.getBasePrice());
            }
            auctionKoiRepository.save(auctionKoiToUpdate);
        }
    }

    @Transactional
    @Override
    public void revokeKoiInAuction(long koiId, long auctionId) {
        AuctionKoi auctionKoi = auctionKoiRepository.findAuctionKoiByAuctionId(auctionId).stream()
                .filter(ak -> ak.getKoi().getId() == koiId).findFirst()
                .orElseThrow(() -> new DataNotFoundException(String.format("""
                        KoiId %d not found in auctionId "%d"
                        """, koiId, auctionId)));

        if (auctionKoi.getRevoked() == 1){
            throw new MalformBehaviourException("Koi is already revoked");
        }

        auctionKoi.setRevoked(1); // soft delete
        auctionKoiRepository.save(auctionKoi);
    }

    @Override
    public Page<KoiInAuctionResponse> getKoiByKeyword(String keyword, Pageable pageable) {
        return koiRepository.findByKeyword(keyword, pageable);
    }

    @Override
    public BidMethodQuantityResponse findQuantityByBidMethod() {
        List<AuctionKoi> auctionKois = auctionKoiRepository.findAll();
        long ascendingBid = auctionKois.stream()
                .filter(auctionKoi -> auctionKoi.getBidMethod() == EBidMethod.ASCENDING_BID).count();
        long descendingBid = auctionKois.stream()
                .filter(auctionKoi -> auctionKoi.getBidMethod() == EBidMethod.DESCENDING_BID).count();
        long fixedPrice = auctionKois.stream()
                .filter(auctionKoi -> auctionKoi.getBidMethod() == EBidMethod.FIXED_PRICE).count();

        return new BidMethodQuantityResponse(auctionKois.size(), ascendingBid, descendingBid, fixedPrice);
    }

    @Override
    public boolean findKoiInAuction(long koiId) {
        for (AuctionKoi auctionKoi : auctionKoiRepository.findAll()) {
            if (auctionKoi.getKoi().getId() == koiId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void revokeKoiInAuction(long koiId) {

        auctionKoiRepository.findAll().stream()
                .filter(auctionKoi -> auctionKoi.getKoi().getId() == koiId)
                .findAny();


        for (AuctionKoi auctionKoi : auctionKoiRepository.findAll()) {
            if (auctionKoi.getKoi().getId() == koiId) {
                auctionKoi.setRevoked(1);
                auctionKoiRepository.save(auctionKoi);
            }
        }
    }

    @Override
    public List<AuctionKoiResponse> getAuctionKoiByAuctionId(long id) {
        return auctionKoiRepository.findAuctionKoiByAuctionId(id).stream()
                .map(DTOConverter::toAuctionKoiResponse)
                .toList();
    }

    @Override
    public Page<AuctionKoiResponse> getAllAuctionKois(Pageable pageable) {
        Page<AuctionKoi> kois = auctionKoiRepository.findAll(pageable);
        return kois.map(DTOConverter::toAuctionKoiResponse);
    }

    @Override
    public AuctionKoiResponse updateAuctionKoi(long auctionKoiId,
            UpdateAuctionKoiDTO updateAuctionKoiDTO) {
        // find auctionKoi
        AuctionKoi updateAuctionKoi = auctionKoiRepository.findById(auctionKoiId)
                .orElseThrow(() -> new DataNotFoundException("auctionKoi not found: " + auctionKoiId));

        if (updateAuctionKoi.getBidMethod() == EBidMethod.DESCENDING_BID) {
            if (updateAuctionKoi.getCeilPrice() == null) {
                throw new MalformDataException("Ceil price is required for DESCENDING_BID");
            }
            if (updateAuctionKoi.getCurrentBid() > updateAuctionKoi.getCeilPrice()) {
                throw new MalformDataException("Current bid must be less than ceil price");
            }
            if (updateAuctionKoi.getCurrentBid() < updateAuctionKoi.getBasePrice()) {
                throw new MalformDataException("Current bid must be greater than base price");
            }
        }

        updateAuctionKoi.setBasePrice(updateAuctionKoiDTO.basePrice());
        updateAuctionKoi.setBidStep(updateAuctionKoiDTO.bidStep());
        updateAuctionKoi.setBidMethod(EBidMethod.valueOf(updateAuctionKoiDTO.bidMethod()));
        updateAuctionKoi.setCeilPrice(updateAuctionKoiDTO.ceilPrice());
        updateAuctionKoi.setCurrentBid(updateAuctionKoiDTO.currentBid());
        updateAuctionKoi.setCurrentBidderId(updateAuctionKoiDTO.currentBidderId());
        updateAuctionKoi.setRevoked(updateAuctionKoiDTO.revoked());
        updateAuctionKoi.setSold(updateAuctionKoiDTO.isSold());

        return DTOConverter.toAuctionKoiResponse(auctionKoiRepository.save(updateAuctionKoi));
    }

    @Override
    public void deleteAuctionKoi(long id) {
        auctionKoiRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Auction Koi not found"));
        auctionKoiRepository.softDeleteById(id);
    }

    @Override
    public AuctionKoiResponse getAuctionKoiDetailsById(long id) throws DataNotFoundException {
        AuctionKoi auctionKoi = auctionKoiRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Auction Koi not found"));
        return DTOConverter.toAuctionKoiResponse(auctionKoi);
    }

    @Override
    public AuctionKoiResponse getAuctionKoiByAuctionIdAndKoiId(long aid, long id)
            throws DataNotFoundException {
        List<AuctionKoi> auctionKois = auctionKoiRepository.findAuctionKoiByAuctionId(aid);
        AuctionKoi auctionKoi = auctionKois.stream()
                .filter(auctionKoi1 -> auctionKoi1.getId() == id).findFirst()
                .orElseThrow(() -> new DataNotFoundException("Auction Koi not found"));
        return DTOConverter.toAuctionKoiResponse(auctionKoi);
    }

    @Override
    @Transactional
    public boolean updateAuctionKoiStatus(long auctionKoiId, AuctionKoi auctionKoi) {
        AuctionKoi auctionKoiToUpdate = auctionKoiRepository.findById(auctionKoiId)
                .orElseThrow(() -> new DataNotFoundException("Auction Koi not found"));

        if (auctionKoiToUpdate.getAuction().getStatus().equals(EAuctionStatus.ENDED)) {
            if (auctionKoiToUpdate.isSold()) {
                return false;
            }

            // Handle force-ended auctions
            if (auctionKoiToUpdate.getCurrentBidderId() != 0) {
                // There's a valid bidder, mark as sold regardless of bid amount
                auctionKoiToUpdate.setSold(true);
                auctionKoiRepository.save(auctionKoiToUpdate);
                return true;
            } else if (auctionKoiToUpdate.getCurrentBid() > 0) {
                // Has bids but no current bidder (edge case)
            }
        }
        return false;
    }

}
