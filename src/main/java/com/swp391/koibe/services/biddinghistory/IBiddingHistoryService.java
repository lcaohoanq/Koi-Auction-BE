package com.swp391.koibe.services.biddinghistory;

import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.BidHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBiddingHistoryService {
    BidHistory createBidHistory(BidHistory bidHistory) throws DataNotFoundException;
    BidHistory getBidHistoryById(long id);
    Page<BidHistory> getAllBidHistories(Pageable pageable);
    BidHistory updateBidHistory(long id, BidHistory bidHistory);
    void deleteBidHistory(long id);
    boolean existsByName(String name);
    void createBidHistories(List<BidHistory> bidHistories) throws DataNotFoundException;
}
