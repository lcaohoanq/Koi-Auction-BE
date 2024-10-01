package com.swp391.koibe.services.auction;

import com.swp391.koibe.dtos.AuctionDTO;
import com.swp391.koibe.enums.EAuctionStatus;
import com.swp391.koibe.exceptions.MalformDataException;
import com.swp391.koibe.exceptions.base.DataAlreadyExistException;
import com.swp391.koibe.exceptions.base.DataNotFoundException;
import com.swp391.koibe.models.Auction;
import com.swp391.koibe.repositories.AuctionRepository;
import com.swp391.koibe.responses.AuctionResponse;
import com.swp391.koibe.utils.DTOConverter;
import com.swp391.koibe.utils.DateTimeUtils;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    public Auction createAuction(AuctionDTO auctionDTO) throws DataAlreadyExistException {
        LocalDateTime startTime = DateTimeUtils.parseTime(auctionDTO.startTime());
        LocalDateTime endTime = DateTimeUtils.parseTime(auctionDTO.endTime());
        DateTimeUtils.validateAuctionTimes(startTime, endTime);

        //=====================================Consider to check later, still accept the same title
        if (auctionRepository.existsByTitle(auctionDTO.title())) {
            throw new DataAlreadyExistException("Auction's title already exists");
        }

        if(auctionDTO.statusName().equals(EAuctionStatus.ENDED.name())) {
            throw new MalformDataException("Cannot create ended auction");
        }

        // check if status name is valid
        boolean isValidStatus = Arrays.stream(EAuctionStatus.values())
            .anyMatch(status -> status.name().equals(auctionDTO.statusName()));

        if (!isValidStatus) {
            throw new MalformDataException("Invalid auction status name");
        }

        Auction newAuction = Auction.builder()
            .title(auctionDTO.title())
            .startTime(startTime)
            .endTime(endTime)
            .status(EAuctionStatus.valueOf(auctionDTO.statusName()))
            .build();

        return auctionRepository.save(newAuction);
    }

    @Override
    public AuctionResponse getById(long id) throws DataNotFoundException {
        return auctionRepository.findById(id)
            .map(DTOConverter::convertToAuctionDTO)
            .orElseThrow(() -> new DataNotFoundException("Auction not found"));
    }

    @Override
    public Auction findAuctionById(long id) throws DataNotFoundException {
        return auctionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Auction not found"));
    }

    @Override
    public Page<AuctionResponse> getAllAuctions(Pageable pageable) {
        Page<Auction> auctions = auctionRepository.findAll(pageable);
        return auctions.map(DTOConverter::convertToAuctionDTO);
    }


    @Override
    public Auction update(long auctionId, AuctionDTO auctionDTO) throws DataNotFoundException {
        Auction auction = auctionRepository.findById(auctionId)
            .orElseThrow(() -> new DataNotFoundException("Auction not found"));

        LocalDateTime startTime = DateTimeUtils.parseTime(auctionDTO.startTime());
        LocalDateTime endTime = DateTimeUtils.parseTime(auctionDTO.endTime());
        DateTimeUtils.validateAuctionTimes(startTime, endTime);

        // check if status name is valid
        boolean isValidStatus = Arrays.stream(EAuctionStatus.values())
            .anyMatch(status -> status.name().equals(auctionDTO.statusName()));

        if (!isValidStatus) {
            throw new MalformDataException("Invalid auction status name");
        }

        //can update the auction to any status

        auction.setTitle(auctionDTO.title());
        auction.setStartTime(startTime);
        auction.setEndTime(endTime);
        auction.setStatus(EAuctionStatus.valueOf(auctionDTO.statusName()));

        return auctionRepository.save(auction);
    }

    @Override
    public void delete(long id) {
        Auction auction = auctionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Auction not found"));
        auctionRepository.delete(auction);
    }

    @Override
    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }
}
