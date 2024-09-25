package com.swp391.koibe.services.auctionkoidetail;

import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.BidHistory;
import com.swp391.koibe.repositories.BidHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionKoiDetailService implements IAuctionKoiDetailService {

    private final BidHistoryRepository bidHistoryRepository;

    @Override
    public BidHistory createBidHistory(BidHistory bidHistory) throws DataNotFoundException {
        return bidHistoryRepository.save(bidHistory);
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

    @Override
    public void createBidHistories(List<BidHistory> bidHistories) throws DataNotFoundException {
        for (BidHistory bidHistory : bidHistories) {
            createBidHistory(bidHistory);
        }
    }
}
