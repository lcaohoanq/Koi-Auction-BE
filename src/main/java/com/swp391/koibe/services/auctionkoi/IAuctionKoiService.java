package com.swp391.koibe.services.auctionkoi;

import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.AuctionKoi;
import com.swp391.koibe.responses.AuctionKoiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAuctionKoiService {
    AuctionKoi createAuctionKoi(AuctionKoi auctionKoi) throws DataNotFoundException;
    AuctionKoi getAuctionKoiById(long id);
    Page<AuctionKoiResponse> getAllAuctionKois(Pageable pageable);
    AuctionKoi updateAuctionKoi(long id, AuctionKoi auctionKoi);
    void deleteAuctionKoi(long id);
    boolean existsByName(String name);
}
