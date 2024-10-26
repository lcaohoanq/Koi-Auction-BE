package com.swp391.koibe.services.user.breeder;

import static org.junit.jupiter.api.Assertions.*;

import com.swp391.koibe.models.Role;
import com.swp391.koibe.models.User;
import com.swp391.koibe.utils.DTOConverter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BreederServiceTest {

    @MockBean
    private BreederService breederService;

    @BeforeEach
    void setUp() {
        Mockito.reset(breederService);
    }

    @Test
    void getAllBreeders() {
        Role breederRole = Role.builder()
            .id(3L)
            .build();

        List<User> breeders = List.of(
            User.builder().id(1L).role(breederRole).build(),
            User.builder().id(2L).role(breederRole).build(),
            User.builder().id(3L).role(breederRole).build(),
            User.builder().id(4L).role(breederRole).build(),
            User.builder().id(5L).role(breederRole).build(),
            User.builder().id(6L).role(breederRole).build()
        );

        Mockito.when(breederService.getAllBreeders())
                .thenReturn(breeders.stream().map(DTOConverter::convertToUserDTO)
                .toList());

    }

    @Test
    void testGetAllBreeders() {
    }

    @Test
    void findById() {
    }

    @Test
    void createKoi() {
    }

    @Test
    void updateBreeder() {
    }

    @Test
    void updateKoi() {
    }

    @Test
    void deleteBreeder() {
    }

    @Test
    void deleteKoi() {
    }

    @Test
    void getKoisByBreederID() {
    }

    @Test
    void testGetKoisByBreederID() {
    }

    @Test
    void getKoisByBreederIdAndStatus() {
    }
}