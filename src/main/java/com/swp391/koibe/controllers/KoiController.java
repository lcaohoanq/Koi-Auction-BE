package com.swp391.koibe.controllers;

import com.github.javafaker.Faker;
import com.swp391.koibe.dtos.KoiDTO;
import com.swp391.koibe.responses.KoiResponse;
import com.swp391.koibe.services.koi.IKoiService;
import com.swp391.koibe.utils.SampleDataStorage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("${api.prefix}/kois")
@RestController
@RequiredArgsConstructor
public class KoiController {

    private final IKoiService koiService;

    @PostMapping("/generateFakeKois")
    private ResponseEntity<String> generateFakeKois() {
        Faker faker = new Faker();

        SampleDataStorage.Koi koi = new SampleDataStorage.Koi();

        for (int i = 1; i < 300; i++) {
            String koiName = koi.getKoiNames().get(faker.number().numberBetween(0, koi.getKoiNames().size()));
//            if(koiService.existsByName(koiName)) {
//                continue;
//            }
            KoiDTO koiDTO = KoiDTO.builder()
                .name(koiName)
                .price((float)faker.number().numberBetween(10, 90_000_000))
                .EKoiStatus(koi.getKoiStatusList()[faker.number().numberBetween(0, koi.getKoiStatusList().length)])
                .isDisplay(faker.number().numberBetween(0, 2))
                .thumbnail("https://mjjlqhnswgbzvxfujauo.supabase.co/storage/v1/object/public/auctions/48/photos/Sanke%2040cm.png")
                .sex(koi.getGenders()[faker.number().numberBetween(0, koi.getGenders().length)])
                .length(faker.number().numberBetween(1, 100))
                .age(faker.number().numberBetween(1, 231)) //1-230
                .description(faker.lorem().sentence())
                .ownerId((long) faker.number().numberBetween(1, 4)) //userId from 1-3
                .categoryId(faker.number().numberBetween(1, 11)) //1-10 categories
                .build();
            try{
                koiService.createKoi(koiDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake kois generated");
    }

    //pagination kois
    @GetMapping("") //kois/?page=0&limit=10
    public ResponseEntity<List<KoiResponse>> getAllKois(
        @RequestParam("page") int page,
        @RequestParam("limit") int limit
    ) {
        try{
            PageRequest pageRequest = PageRequest.of(page, limit);
            Page<KoiResponse> categories = koiService.getAllKois(pageRequest);
            return ResponseEntity.ok(categories.getContent());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }



}
