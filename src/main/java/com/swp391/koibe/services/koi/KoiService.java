package com.swp391.koibe.services.koi;

import com.swp391.koibe.constants.ErrorMessage;
import com.swp391.koibe.dtos.KoiDTO;
import com.swp391.koibe.dtos.KoiImageDTO;
import com.swp391.koibe.exceptions.InvalidParamException;
import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.models.Category;
import com.swp391.koibe.models.Koi;
import com.swp391.koibe.models.KoiImage;
import com.swp391.koibe.models.User;
import com.swp391.koibe.repositories.CategoryRepository;
import com.swp391.koibe.repositories.KoiImageRepository;
import com.swp391.koibe.repositories.KoiRepository;
import com.swp391.koibe.repositories.UserRepository;
import com.swp391.koibe.responses.KoiResponse;
import com.swp391.koibe.utils.DTOConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KoiService implements IKoiService {

    private final KoiRepository koiRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final KoiImageRepository koiImageRepository;

    @Override
    public Koi createKoi(KoiDTO koiDTO) throws DataNotFoundException {

        User existedUser = userRepository.findById(koiDTO.getOwnerId())
            .orElseThrow(() ->
                             new DataNotFoundException("User not found: " + koiDTO.getOwnerId()));

        Category existedCategory = categoryRepository.findById(koiDTO.getCategoryId())
            .orElseThrow(() ->
                             new DataNotFoundException("Category not found: " + koiDTO.getCategoryId()));

        Koi newKoi = Koi.builder()
            .name(koiDTO.getName())
            .price(koiDTO.getPrice())
//            .trackingStatus(koiDTO.getKoiTrackingStatus())
            .isDisplay(koiDTO.getIsDisplay())
            .thumbnail(koiDTO.getThumbnail())
            .sex(koiDTO.getSex())
            .length(koiDTO.getLength())
            .age(koiDTO.getAge())
            .description(koiDTO.getDescription())
            .owner(existedUser)
            .category(existedCategory)
            .build();
        return koiRepository.save(newKoi);
    }

    @Override
    public KoiResponse getKoiById(long id) {
        return koiRepository.findById(id).map(DTOConverter::convertToKoiDTO).get();
    }

    @Override
    public Page<KoiResponse> getAllKois(Pageable pageable) {
        Page<Koi> kois = koiRepository.findAll(pageable);
        return kois.map(DTOConverter::convertToKoiDTO);
    }

    @Override
    public Koi updateKoi(long id, KoiDTO koiDTO) {
        return null;
    }

    @Override
    public void deleteKoi(long id) {

    }

    @Override
    public boolean existsByName(String name) {
        return koiRepository.existsByName(name);
    }

    @Override
    public List<Koi> getAllKois() {
        return koiRepository.findAll();
    }

    @Override
    public KoiImage createKoiImage(Long koiId, KoiImageDTO koiImageDTO) throws Exception {
        Koi existingKoi = koiRepository
            .findById(koiId)
            .orElseThrow(() ->
                             new DataNotFoundException("Category not found: " + koiImageDTO.getKoiId()));

        KoiImage newKoiImage = KoiImage.builder()
            .koi(existingKoi)
            .imageUrl(koiImageDTO.getImageUrl())
            .build();
        //khong cho insert qua 5 anh cho mot san pham
        int size = koiImageRepository.findByKoiId(koiId).size();
        if (size >= KoiImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException(
                ErrorMessage.MAXIMUM_IMAGES_PER_PRODUCT
                    + KoiImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return koiImageRepository.save(newKoiImage);
    }
}
