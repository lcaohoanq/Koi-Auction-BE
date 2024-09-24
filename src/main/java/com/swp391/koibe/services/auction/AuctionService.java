package com.swp391.koibe.services.auction;

import com.swp391.koibe.exceptions.exist.DataAlreadyExistException;
import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.Auction;
import com.swp391.koibe.repositories.AuctionRepository;
import com.swp391.koibe.responses.AuctionResponse;
import com.swp391.koibe.utils.DTOConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class AuctionService implements IAuctionService {
    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    @Override
    public Auction createAuction(Auction auction) throws DataAlreadyExistException {
        return auctionRepository.save(auction);
    }

    @Override
    public Auction getById(int id) throws DataNotFoundException {
        return auctionRepository.findById((long) id)
                .orElseThrow(() -> new DataNotFoundException("Auction not found: " + id));
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
}
