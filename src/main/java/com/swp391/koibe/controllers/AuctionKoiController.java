package com.swp391.koibe.controllers;

import com.swp391.koibe.dtos.KoiDTO;
import com.swp391.koibe.enums.EAuctionStatus;
import com.swp391.koibe.enums.EBidMethod;
import com.swp391.koibe.enums.EKoiStatus;
import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.Auction;
import com.swp391.koibe.models.AuctionKoi;
import com.swp391.koibe.models.Koi;
import com.swp391.koibe.responses.AuctionKoiResponse;
import com.swp391.koibe.responses.AuctionResponse;
import com.swp391.koibe.services.auction.IAuctionService;
import com.swp391.koibe.services.auctionkoi.IAuctionKoiService;
import com.swp391.koibe.services.koi.IKoiService;
import com.swp391.koibe.services.user.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/auctionkois")
@RequiredArgsConstructor
public class AuctionKoiController {

    private final IAuctionKoiService auctionKoiService;
    private final IAuctionService auctionService;
    private final IKoiService koiService;
    private final IUserService userService;

    @PostMapping("/generateFakeAuctionKois")
    public ResponseEntity<String> generateFakeAuctionKois() throws DataNotFoundException {
        List<Auction> auctions = auctionService.getAllAuctions();
        List<Koi> kois = koiService.getAllKois();
        Set<Long> usedKoiIds = new HashSet<>();
        Random random = new Random();

        for (Auction auction : auctions) {
            List<Koi> availableKois = new ArrayList<>(kois);
            availableKois.removeIf(koi -> usedKoiIds.contains(koi.getId()));

            int koiCount = Math.min(10, availableKois.size());
            for (int i = 0; i < koiCount; i++) {
                Koi randomKoi = availableKois.remove(random.nextInt(availableKois.size()));
                usedKoiIds.add(randomKoi.getId());

                boolean isSold = random.nextBoolean();

                AuctionKoi auctionKoi = AuctionKoi.builder()
                        .auction(auction)
                        .koi(randomKoi)
                        .basePrice(randomKoi.getPrice())
                        .bidMethod(EBidMethod.ASCENDING_BID)
                        .currentBid(isSold ? randomBid(randomKoi.getPrice()) : 0)
                        .currentBidderId(isSold ? random.nextLong(1, 16) : null)
                        .isSold(isSold)
                        .build();

                // active the koi when it is in auction
                randomKoi.setStatus(EKoiStatus.VERIFIED);

                if (isSold) {
                    randomKoi.setStatus(EKoiStatus.SOLD);
                    randomKoi.setOwner(userService.getUserById(auctionKoi.getCurrentBidderId()));
                    kois.remove(randomKoi);
                }
                // convert Koi to KoiDTO
                KoiDTO randomKoiDTO = KoiDTO.builder()
                        .name(randomKoi.getName())
                        .price(randomKoi.getPrice())
                        .isDisplay(randomKoi.getIsDisplay())
                        .thumbnail(randomKoi.getThumbnail())
                        .sex(randomKoi.getSex())
                        .length(randomKoi.getLength())
                        .age(randomKoi.getAge())
                        .isDisplay(randomKoi.getIsDisplay())
                        .description(randomKoi.getDescription())
                        .categoryId(randomKoi.getCategory().getId())
                        .ownerId(randomKoi.getOwner().getId())
                        .build();

                try {
                    koiService.updateKoi(randomKoi.getId(), randomKoiDTO);
                    auctionKoiService.createAuctionKoi(auctionKoi);
                } catch (Exception e) {
                    log.error("Error creating AuctionKoi or updating Koi: " + e.getMessage(), e);
                    return ResponseEntity.status(500)
                            .body("Error creating AuctionKoi or updating Koi: " + e.getMessage());
                }
            }
        }

        return ResponseEntity.ok("Fake AuctionKoi data generated successfully");
    }

    private int randomBid(float basePrice) {
        return (int) (basePrice * (1.5));
    }

    @GetMapping("") // /auctions/?page=1&limit=10
    public ResponseEntity<List<AuctionKoiResponse>> getAllAuctions(
            @RequestParam int page,
            @RequestParam int limit) {
        try {
            PageRequest pageRequest = PageRequest.of(page, limit);
            Page<AuctionKoiResponse> auctionKois = auctionKoiService.getAllAuctionKois(pageRequest);
            return ResponseEntity.ok(auctionKois.getContent());
        }
        catch (Exception e) {
            log.error("Error getting all auctionkois    : " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

}