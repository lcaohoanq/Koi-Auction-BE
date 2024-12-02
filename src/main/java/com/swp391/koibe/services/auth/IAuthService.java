package com.swp391.koibe.services.auth;

import com.swp391.koibe.dtos.UserRegisterDTO;
import com.swp391.koibe.models.User;

public interface IAuthService {

    User createUser(UserRegisterDTO userRegisterDTO) throws Exception;
    String login(String email, String password) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    void logout(String token, User user) throws Exception;

}
