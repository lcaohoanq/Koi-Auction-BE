package com.swp391.koibe.controllers;

import com.github.javafaker.Faker;
import com.swp391.koibe.repositories.KoiImageRepository;
import com.swp391.koibe.services.koiimage.IKoiImageService;
import com.swp391.koibe.utils.SampleDataStorage;
import com.swp391.koibe.utils.SampleDataStorage.KoiImage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/koiimage")
@RequiredArgsConstructor
public class KoiImageController {

    private final IKoiImageService koiImageService;
    private final KoiImageRepository koiImageRepository;

    @PostMapping("/generateFakeKoiImages")
    public ResponseEntity<?> generateFakeKoiImages() {
        Faker faker = new Faker();
        SampleDataStorage.KoiImage koiImage = new KoiImage();
        for(int i = 1; i <= 100; i++){

        }
    }

}
