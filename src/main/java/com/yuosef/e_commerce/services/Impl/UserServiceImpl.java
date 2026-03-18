package com.yuosef.e_commerce.services.Impl;

import com.yuosef.e_commerce.config.JWT.TokenHandler;
import com.yuosef.e_commerce.models.*;
import com.yuosef.e_commerce.repository.AuthorityDao;
import com.yuosef.e_commerce.repository.OAuthCodeDao;
import com.yuosef.e_commerce.repository.UserDao;
import com.yuosef.e_commerce.models.Dtos.*;
import com.yuosef.e_commerce.models.Mappers.Usermapper;
import com.yuosef.e_commerce.services.UserService;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final TokenHandler tokenHandler;
    private final AuthorityDao authorityRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final OAuthCodeDao oAuthCodeRepository;
    private final OtpRedisService otpRedisService;
    private final EmailService emailService;

    @Override
    public AuthResponse login(LoginInfo loginInfo) {
        User user = userDao.findUserByEmail(loginInfo.email()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getAuthProvider() == AuthProvider.GOOGLE) {
            throw new IllegalArgumentException(
                    "This account uses Google login. Please sign in with Google."
            );
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginInfo.email(), loginInfo.password()));
        String accessToken = tokenHandler.createToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthResponse(accessToken,refreshToken.getToken());
    }

    @Override
    public UserDto createUser(UserAccountInfo userAccInfo) throws SystemException {
        Optional<User> user = userDao.findUserByEmail(userAccInfo.email());
        if (user.isPresent()) {
            throw new SystemException("this email " + userAccInfo.email() + " is already in use");
        }
            // client not exist
        Authority userRole = authorityRepository.findByUserRole("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER not found"));

        List<Authority> auths = List.of(userRole);

        User user2 = User.builder()
                .name(userAccInfo.name())
                .email(userAccInfo.email())
                .mobileNumber(userAccInfo.phoneNumber())
                .pwd(passwordEncoder.encode(userAccInfo.password()))
                .authorities(auths).build();
        return Usermapper.toDto(userDao.save(user2));
    }
    @Override
    public UserDto createUserOtp(UserAccountInfo user){
        Authority userRole = authorityRepository.findByUserRole("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER not found"));

        List<Authority> auths = List.of(userRole);

        User user2 = User.builder()
                .name(user.name())
                .email(user.email())
                .mobileNumber(user.phoneNumber())
                .pwd(passwordEncoder.encode(user.password()))
                .authorities(auths).build();
        return Usermapper.toDto(userDao.save(user2));
    }

    @Override
    public String createOtp(UserAccountInfo request) throws SystemException {
        checkUser(request);
        String otp = otpRedisService.generateOtp();
        PendingUser pendingUser = new PendingUser();
        pendingUser.setUser(request);
        pendingUser.setOtp(otp);
        otpRedisService.savePendingUser(request.email(), pendingUser);
        emailService.sendOtp(request.email(), otp);
        return "OTP sent successfully";
    }
    @Override
    public UserDto verifyOtp(VerifyOtpRequest request){
        PendingUser pendingUser = otpRedisService.getPendingUser(request.email());
        if (pendingUser == null) {
            throw new IllegalArgumentException("OTP expired");
        }

        if (!pendingUser.getOtp().equals(request.otp())) {
            throw new IllegalArgumentException("Invalid OTP");
        }
        UserDto user=createUserOtp(pendingUser.getUser());
        otpRedisService.deletePendingUser(request.email());
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> user = userDao.findUserByEmail(email);
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        // verify the refresh token is valid and not expired
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.refreshToken());

        // get the user from the refresh token
        User user = refreshToken.getUser();

        // generate a new access token
        String newAccessToken = tokenHandler.createToken(user);

        // return new access token and same refresh token
        return new AuthResponse(newAccessToken, refreshToken.getToken());
    }
    @Override
    public void logout(User user) {
        refreshTokenService.deleteByUser(user);
    }

    @Transactional
    @Override
    public AuthResponse exchangeCode(String code) {
        // find the code
        OAuthCode oAuthCode = oAuthCodeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid code"));

        // check if expired
        if (oAuthCode.isExpired()) {
            oAuthCodeRepository.delete(oAuthCode);
            throw new IllegalArgumentException("Code expired, please login again");
        }

        // load the user
        User user = userDao.findUserByEmail(oAuthCode.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // delete code one time use only
        oAuthCodeRepository.delete(oAuthCode);

        // generate tokens
        String accessToken = tokenHandler.createToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Override
    public void checkUser(UserAccountInfo userAccInfo) throws SystemException {
        Optional<User> user = userDao.findUserByEmail(userAccInfo.email());
        if (user.isPresent()) {
            throw new SystemException("this email " + userAccInfo.email() + " is already in use");
        }
    }

}
