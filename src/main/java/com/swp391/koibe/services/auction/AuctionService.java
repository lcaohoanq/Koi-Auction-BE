package com.swp391.koibe.services.auction;

import com.swp391.koibe.exceptions.base.DataAlreadyExistException;
import com.swp391.koibe.exceptions.base.DataNotFoundException;
import com.swp391.koibe.models.Auction;
import com.swp391.koibe.repositories.AuctionRepository;
import com.swp391.koibe.responses.AuctionResponse;
import com.swp391.koibe.utils.DTOConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuctionService implements IAuctionService {
    private final AuctionRepository auctionRepository;

    @Override
    public Auction createAuction(Auction auction) throws DataAlreadyExistException {
        return auctionRepository.save(auction);
    }

    @Override
    public AuctionResponse getById(long id) throws DataNotFoundException {
        return auctionRepository.findById(id)
            .map(DTOConverter::convertToAuctionDTO)
            .orElseThrow(() -> new DataNotFoundException("Auction not found"));
    }

    @Override
    public Page<AuctionResponse> getAllAuctions(Pageable pageable) {
        Page<Auction> auctions = auctionRepository.findAll(pageable);
        return auctions.map(DTOConverter::convertToAuctionDTO);
    }


    @Override
    public Auction update(int auctionId, Auction auction) throws DataNotFoundException {
        return null;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }
}
