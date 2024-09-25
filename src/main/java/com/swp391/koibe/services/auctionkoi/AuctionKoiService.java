package com.swp391.koibe.services.auctionkoi;

import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.AuctionKoi;
import com.swp391.koibe.repositories.AuctionKoiRepository;
import com.swp391.koibe.repositories.AuctionRepository;
import com.swp391.koibe.responses.AuctionKoiResponse;
import com.swp391.koibe.responses.AuctionResponse;
import com.swp391.koibe.utils.DTOConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionKoiService implements IAuctionKoiService {

    private final AuctionKoiRepository auctionKoiRepository;
    private final AuctionRepository auctionRepository;

    @Override
    public AuctionKoi createAuctionKoi(AuctionKoi auctionKoi) throws DataNotFoundException {
        return auctionKoiRepository.save(auctionKoi);
    }

    @Override
    public AuctionKoi getAuctionKoiById(long id) {
        return null;
    }

    @Override
    public Page<AuctionKoiResponse> getAllAuctionKois(Pageable pageable) {
        Page<AuctionKoi> kois = auctionKoiRepository.findAll(pageable);
        return kois.map(DTOConverter::convertToAuctionKoiDTO);
    }

    @Override
    public AuctionKoi updateAuctionKoi(long id, AuctionKoi auctionKoi) {
        return null;
    }

    @Override
    public void deleteAuctionKoi(long id) {

    }

    @Override
    public boolean existsByName(String name) {
        return false;
    }

    @Override
    public List<AuctionKoi> getAuctionIsSold() {
        // get all auction koi
        return auctionKoiRepository.findAll().stream().filter(auctionKoi -> auctionKoi.isSold()).toList();
    }
}
