package com.swp391.koibe.services.auth;

import com.swp391.koibe.components.JwtTokenUtils;
import com.swp391.koibe.components.LocalizationUtils;
import com.swp391.koibe.constants.Regex;
import com.swp391.koibe.dtos.UserRegisterDTO;
import com.swp391.koibe.enums.EmailCategoriesEnum;
import com.swp391.koibe.enums.UserRole;
import com.swp391.koibe.enums.UserStatus;
import com.swp391.koibe.exceptions.ExpiredTokenException;
import com.swp391.koibe.exceptions.MalformBehaviourException;
import com.swp391.koibe.exceptions.PasswordWrongFormatException;
import com.swp391.koibe.exceptions.PermissionDeniedException;
import com.swp391.koibe.exceptions.base.DataNotFoundException;
import com.swp391.koibe.models.Otp;
import com.swp391.koibe.models.Role;
import com.swp391.koibe.models.User;
import com.swp391.koibe.repositories.RoleRepository;
import com.swp391.koibe.repositories.SocialAccountRepository;
import com.swp391.koibe.repositories.UserRepository;
import com.swp391.koibe.services.mail.IMailService;
import com.swp391.koibe.services.otp.OtpService;
import com.swp391.koibe.services.role.RoleService;
import com.swp391.koibe.services.token.TokenService;
import com.swp391.koibe.utils.MessageKey;
import com.swp391.koibe.utils.OtpUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final RoleRepository roleRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final LocalizationUtils localizationUtils;
    private final IMailService mailService;
    private final OtpService otpService;
    private final RoleService roleService;
    private final TokenService tokenService;

    @Override
    @Transactional
    public User createUser(UserRegisterDTO userRegisterDTO) throws Exception {
        if(!userRegisterDTO.password().matches(Regex.PASSWORD_REGEX)){
            throw new PasswordWrongFormatException("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        }

        if (!userRegisterDTO.password().equals(userRegisterDTO.confirmPassword())) {
            throw new MalformBehaviourException("Password and confirm password must be the same");
        }

        String email = userRegisterDTO.email();
        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        Role role = roleRepository.findById(userRegisterDTO.roleId())
            .orElseThrow(() -> new DataNotFoundException("Role not found"));

        if (role.getUserRole() == UserRole.MANAGER
            || role.getUserRole() == UserRole.STAFF
            || role.getUserRole() == UserRole.BREEDER
        ) {
            log.error("Cannot directly create this type account");
            throw new PermissionDeniedException("Cannot directly create this type account");
        }

        User newUser = User.builder()
            .firstName(userRegisterDTO.firstName())
            .lastName(userRegisterDTO.lastName())
            .email(userRegisterDTO.email())
            .password(userRegisterDTO.password())
            .address(userRegisterDTO.address())
            .isActive(true)
            .isSubscription(((userRegisterDTO.isSubscription() == null ? 0 : 1) != 0)) //force
            // subscription
            .status(UserStatus.UNVERIFIED)
            .dob(userRegisterDTO.dateOfBirth())
            .avatarUrl(Optional.ofNullable(userRegisterDTO.avatarUrl())
                           .orElse("https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg"))
            .googleAccountId(userRegisterDTO.googleAccountId())
            .accountBalance(0L) //new user has 0 money when register
            .build();

        newUser.setRole(role);

        // check if user are log in without google account, then encode password
        if (userRegisterDTO.googleAccountId() == 0) {
            newUser.setPassword(passwordEncoder.encode(userRegisterDTO.password()));
        }

        User savedUser = userRepository.save(newUser);

        // send OTP email
        String otp = OtpUtils.generateOtp();
        Context context = new Context();
        context.setVariable("name", savedUser.getFirstName());
        context.setVariable("otp", otp);

        mailService.sendMail(
            savedUser.getEmail(),
            "Verify your email",
            EmailCategoriesEnum.OTP.getType(),
            context
        );

        log.info("Send email otp to {}", savedUser.getEmail());

        Otp otpEntity = Otp.builder()
            .email(savedUser.getEmail())
            .otp(otp)
            .expiredAt(LocalDateTime.now().plusMinutes(5))
            .isUsed(false)
            .isExpired(false)
            .build();

        otpService.createOtp(otpEntity);

        return savedUser;
    }

    @Override
    public String login(String email, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKey.WRONG_PHONE_PASSWORD));
        }
        User existingUser = optionalUser.get();
        if (existingUser.getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException("Wrong email or password");
            }
        }

        if (!optionalUser.get().isActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKey.USER_IS_LOCKED));
        }

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(email, password, existingUser.getAuthorities());
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtils.generateToken(existingUser);
    }

    //Token
    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String email = jwtTokenUtils.extractEmail(token);
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception(
                localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
            );
        }
    }

    @Override
    public void logout(String token, User user) throws Exception {

        if(jwtTokenUtils.isTokenExpired(token)){
            throw new ExpiredTokenException("Token is expired");
        }

        if(!jwtTokenUtils.validateToken(token, user)){
            throw new PermissionDeniedException("Token is invalid");
        }

        tokenService.deleteToken(token, user);
    }


}
