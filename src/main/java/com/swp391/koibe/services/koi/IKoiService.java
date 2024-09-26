package com.swp391.koibe.services.koi;

import com.swp391.koibe.dtos.KoiDTO;
import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.Koi;
import com.swp391.koibe.responses.KoiResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IKoiService {

    Koi createKoi(KoiDTO koiDTO) throws DataNotFoundException;
    KoiResponse getKoiById(long id);
    Page<KoiResponse> getAllKois(Pageable pageable);
    Koi updateKoi(long id, KoiDTO koiDTO);
    void deleteKoi(long id);
    boolean existsByName(String name);
    List<Koi> getAllKois();

}
