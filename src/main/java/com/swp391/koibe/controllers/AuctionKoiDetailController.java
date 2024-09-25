package com.swp391.koibe.controllers;

import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.AuctionKoi;
import com.swp391.koibe.models.User;
import com.swp391.koibe.services.auctionkoi.IAuctionKoiService;
import com.swp391.koibe.services.user.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/auctionkoidetails")
@RequiredArgsConstructor
public class AuctionKoiDetailController {

    private final IAuctionKoiService auctionKoiService;
    private final IUserService userService;

    @PostMapping("/generateFakeAuctionKoiDetails")
    public ResponseEntity<String> generateFakeAuctionKoiDetails() throws DataNotFoundException {
        List<AuctionKoi> auctionKois = auctionKoiService.getAllAuctionKois();
        List<User> users = userService.getAllUsers();
        Set<Long> usedUserIds = new HashSet<>();
        Random random = new Random();

        for (AuctionKoi auctionKoi : auctionKois) {
            List<User> availableUsers = new ArrayList<>(users);
            availableUsers.removeIf(user -> usedUserIds.contains(user.getId()));

            int userCount = Math.min(10, availableUsers.size());
            for (int i = 0; i < userCount; i++) {
                User randomUser = availableUsers.remove(random.nextInt(availableUsers.size()));
                usedUserIds.add(randomUser.getId());

                int bidStep = random.nextInt(1000);
                LocalDateTime bidTime = LocalDateTime.now().minusDays(random.nextInt(30));
                BidHistory bidHistory = BidHistory.builder()
                        .bidStep(bidStep)
                        .bidTime(bidTime)
                        .bidder(randomUser)
                        .auctionKoi(auctionKoi)
                        .build();

                bidHistoryService.saveBidHistory(bidHistory);
            }
        }

        return ResponseEntity.ok("Fake auction koi details generated");
    }

}
