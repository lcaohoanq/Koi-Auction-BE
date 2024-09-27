package com.swp391.koibe.services.user;

import com.swp391.koibe.components.JwtTokenUtils;
import com.swp391.koibe.components.LocalizationUtils;
import com.swp391.koibe.dtos.UpdateUserDTO;
import com.swp391.koibe.dtos.UserRegisterDTO;
import com.swp391.koibe.enums.ProviderName;
import com.swp391.koibe.enums.UserStatus;
import com.swp391.koibe.exceptions.ExpiredTokenException;
import com.swp391.koibe.exceptions.InvalidPasswordException;
import com.swp391.koibe.exceptions.notfound.DataNotFoundException;
import com.swp391.koibe.exceptions.PermissionDeniedException;
import com.swp391.koibe.models.Role;
import com.swp391.koibe.models.SocialAccount;
import com.swp391.koibe.models.User;
import com.swp391.koibe.repositories.RoleRepository;
import com.swp391.koibe.repositories.SocialAccountRepository;
import com.swp391.koibe.repositories.UserRepository;

import com.swp391.koibe.utils.MessageKeys;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final RoleRepository roleRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final LocalizationUtils localizationUtils;

    @Override
    public User createUser(UserRegisterDTO userRegisterDTO) throws Exception {
        String email = userRegisterDTO.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        Role role = roleRepository.findById(userRegisterDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        if(role.getName().toUpperCase().equals(Role.MANAGER)){
            log.error("Cannot create manager account");
            throw new PermissionDeniedException("Cannot create manager account");
        }

        User newUser = User.builder()
                .firstName(userRegisterDTO.getFirstName())
                .lastName(userRegisterDTO.getLastName())
                .email(userRegisterDTO.getEmail())
                .password(userRegisterDTO.getPassword())
                .address(userRegisterDTO.getAddress())
                .status(UserStatus.UNVERIFIED)
                .dob(userRegisterDTO.getDateOfBirth())
                .avatarUrl(userRegisterDTO.getAvatarUrl() == null ? "https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg" : userRegisterDTO.getAvatarUrl())
                .googleAccountId(userRegisterDTO.getGoogleAccountId())
                .accountBalance(0L) //new user has 0 money when register
                .build();

        newUser.setRole(role);

        // check if user are log in without google account, then encode password
        if (userRegisterDTO.getGoogleAccountId() == 0) {
            String password = userRegisterDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String email, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
        }
        User existingUser = optionalUser.get();
        if (existingUser.getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException("Wrong email or password");
            }
        }

        if(!optionalUser.get().isActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password, existingUser.getAuthorities());
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtils.generateToken(existingUser);
    }

    @Override
    public String loginOrRegisterGoogle(String email, String name, String googleId, String avatarUrl) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;
        SocialAccount socialAccount;

        if (optionalUser.isEmpty()) {
            // Register new user
            Role memberRole = roleRepository.findByName("MEMBER")
                    .orElseThrow(() -> new DataNotFoundException("Default MEMBER role not found"));

            User newUser = User.builder()
                    .firstName(name)
                    .email(email)
                    .googleAccountId(1)
                    .avatarUrl(avatarUrl)
                    .role(memberRole)
                    .accountBalance(0L)
                    .build();

            SocialAccount newSocialAccount = SocialAccount.builder()
                    .providerName(ProviderName.GOOGLE)
                    .name(name)
                    .email(email)
                    .build();


            user = userRepository.save(newUser);
            socialAccountRepository.save(newSocialAccount);
        } else {
            // Login existing user
            user = optionalUser.get();
            if (user.getGoogleAccountId() != 1) {
                throw new PermissionDeniedException("Cannot login with google account");
            }
        }

        return jwtTokenUtils.generateToken(user);
    }

    @Override
    public User getUserById(long id) throws DataNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        // Find the existing user by userId
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Check if the email is being changed and if it already exists for another user
        String newEmail = updatedUserDTO.getEmail();

        if(newEmail != null && !newEmail.isEmpty()){
            if (!existingUser.getEmail().equals(newEmail) &&
                userRepository.existsByEmail(newEmail)) {
                throw new DataIntegrityViolationException("Email already exists");
            }
            existingUser.setEmail(newEmail);
        }

        // Check if the phone number is being changed and if it already exists for another user
        String newPhoneNumber = updatedUserDTO.getPhoneNumber();

        if(newPhoneNumber != null && newPhoneNumber.isEmpty()){
            if (!existingUser.getPhoneNumber().equals(newPhoneNumber) &&
                userRepository.existsByPhoneNumber(newPhoneNumber)) {
                throw new DataIntegrityViolationException("Phone number already exists");
            }
            existingUser.setPhoneNumber(newPhoneNumber);
        }

        // Update user information based on the DTO
        if (updatedUserDTO.getFirstName() != null) {
            existingUser.setFirstName(updatedUserDTO.getFirstName());
        }
        if (updatedUserDTO.getLastName() != null) {
            existingUser.setLastName(updatedUserDTO.getLastName());
        }
        if (newPhoneNumber != null) {
            existingUser.setPhoneNumber(newPhoneNumber);
        }
        if (updatedUserDTO.getAddress() != null) {
            existingUser.setAddress(updatedUserDTO.getAddress());
        }
        if(updatedUserDTO.getStatus() != null){
            existingUser.setStatus(UserStatus.valueOf(updatedUserDTO.getStatus()));
        }
        if (updatedUserDTO.getDob() != null) {
            existingUser.setDob(updatedUserDTO.getDob());
        }
        if(updatedUserDTO.getAvatarUrl() != null){
            existingUser.setAvatarUrl(updatedUserDTO.getAvatarUrl());
        }
        if (updatedUserDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(updatedUserDTO.getGoogleAccountId());
        }

        // Update the password if it is provided in the DTO
        if (updatedUserDTO.getPassword() != null
            && !updatedUserDTO.getPassword().isEmpty()) {
            if(!updatedUserDTO.getPassword().equals(updatedUserDTO.getConfirmPassword())) {
                throw new DataNotFoundException("Password and confirm password must be the same");
            }
            String newPassword = updatedUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        //existingUser.setRole(updatedRole);
        // Save the updated user
        return userRepository.save(existingUser);
    }

    //Token
    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtils.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String email = jwtTokenUtils.extractEmail(token);
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found");
        }
    }

    @Override
    public Page<User> findAll(String keyword, Pageable pageable) throws Exception {
        return null;
    }

    @Override
    public void resetPassword(Long userId, String newPassword)
        throws InvalidPasswordException, DataNotFoundException {

    }

    @Override
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {

    }

}
