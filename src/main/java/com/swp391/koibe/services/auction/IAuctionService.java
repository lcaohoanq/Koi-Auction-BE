package com.swp391.koibe.services.auction;

//import com.swp391.koibe.dtos.AuctionDTO;
import com.swp391.koibe.exceptions.exist.DataAlreadyExistException;
import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.Auction;
import com.swp391.koibe.responses.AuctionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAuctionService {

        Auction createAuction(Auction auction) throws DataAlreadyExistException;

        Auction getById(int id) throws DataNotFoundException;

        Page<AuctionResponse> getAllAuctions(Pageable pageable);

        Auction update(int auctionId, Auction auction) throws DataNotFoundException;

        void delete(int id);
}
