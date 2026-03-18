package com.yuosef.e_commerce.services;

import com.yuosef.e_commerce.models.Dtos.*;
import com.yuosef.e_commerce.models.User;
import jakarta.transaction.SystemException;

public interface UserService {
    AuthResponse login(LoginInfo loginInfo);
    UserDto createUser(UserAccountInfo clientAccInfo) throws SystemException;
    String createOtp(UserAccountInfo request) throws SystemException;
    UserDto verifyOtp(VerifyOtpRequest request);
    UserDto createUserOtp(UserAccountInfo user);
    User getUserByEmail(String email) throws SystemException;
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(User user);
    AuthResponse exchangeCode(String code);
    void checkUser(UserAccountInfo request) throws SystemException;
}
