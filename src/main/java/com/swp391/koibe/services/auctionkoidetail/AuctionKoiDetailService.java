package com.swp391.koibe.services.auctionkoidetail;

import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.BidHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class AuctionKoiDetailService implements IAuctionKoiDetailService {
    @Override
    public BidHistory createBidHistory(BidHistory bidHistory) throws DataNotFoundException {
        return null;
    }

    @Override
    public BidHistory getBidHistoryById(long id) {
        return null;
    }

    @Override
    public Page<BidHistory> getAllBidHistories(Pageable pageable) {
        return null;
    }

    @Override
    public BidHistory updateBidHistory(long id, BidHistory bidHistory) {
        return null;
    }

    @Override
    public void deleteBidHistory(long id) {

    }

    @Override
    public boolean existsByName(String name) {
        return false;
    }
}
