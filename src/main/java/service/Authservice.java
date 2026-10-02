package com.foodbridge.backend.service;

import com.foodbridge.backend.dto.ForgotPasswordRequest;
import com.foodbridge.backend.dto.LoginRequest;
import com.foodbridge.backend.dto.LoginResponse;
import com.foodbridge.backend.dto.ResetPasswordRequest;
import com.foodbridge.backend.entity.PasswordResetToken;
import com.foodbridge.backend.entity.Role;
import com.foodbridge.backend.entity.User;
import com.foodbridge.backend.exception.ApiException;
import com.foodbridge.backend.repository.PasswordResetTokenRepository;
import com.foodbridge.backend.repository.UserRepository;
import com.foodbridge.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int OTP_VALID_MINUTES = 10;

    /**
     * Feature 6: Authenticates any user (supershop staff, donor staff,
     * charity, consumer, admin) by email + password and issues a JWT
     * carrying their role, so role-based access works on every endpoint.
     */
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!user.isActive()) {
            throw new ApiException("This account has been deactivated", HttpStatus.FORBIDDEN);
        }

        String token = jwtUtil.generateToken(user.getEmail(), Map.of(
                "role", user.getRole().name(),
                "name", user.getName()
        ));

        Long supershopId = user.getSupershop() != null ? user.getSupershop().getId() : null;

        return new LoginResponse(token, user.getEmail(), user.getName(), user.getRole(), supershopId);
    }

    /**
     * FOOD-79: Build Secure Admin Login.
     * Same credential check as login(), but hard-refuses any account whose
     * role isn't ADMIN — so a leaked staff/consumer token can never be used
     * against admin-only routes even if someone points it at this endpoint.
     */
    public LoginResponse adminLogin(LoginRequest request) {
        LoginResponse response = login(request);

        if (response.getRole() != Role.ADMIN) {
            throw new ApiException("This login is for administrators only", HttpStatus.FORBIDDEN);
        }

        return response;
    }

    /**
     * Feature 2 (step 1): generate a 6-digit OTP, store it with a 10-minute
     * expiry, and email it to the user.
     */
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("No account found for this email", HttpStatus.NOT_FOUND));

        String otp = generateOtp();

        PasswordResetToken token = PasswordResetToken.builder()
                .otpCode(otp)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES))
                .used(false)
                .build();

        resetTokenRepository.save(token);
        emailService.sendOtp(user.getEmail(), otp);
    }

    /**
     * Feature 2 (step 2): verify the OTP and set the new password.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("No account found for this email", HttpStatus.NOT_FOUND));

        PasswordResetToken token = resetTokenRepository.findByOtpCodeAndUsedFalse(request.getOtpCode())
                .orElseThrow(() -> new ApiException("Invalid or already-used OTP", HttpStatus.BAD_REQUEST));

        if (!token.getUser().getId().equals(user.getId())) {
            throw new ApiException("This OTP does not belong to this account", HttpStatus.BAD_REQUEST);
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("This OTP has expired, please request a new one", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        resetTokenRepository.save(token);
    }

    private String generateOtp() {
        int otp = 100000 + RANDOM.nextInt(900000); // always 6 digits
        return String.valueOf(otp);
    }
}
