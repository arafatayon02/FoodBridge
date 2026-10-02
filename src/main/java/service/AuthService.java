package com.foodbridge.service;

import com.foodbridge.dto.auth.*;
import com.foodbridge.entity.*;
import com.foodbridge.entity.enums.*;
import com.foodbridge.exception.ApiException;
import com.foodbridge.repository.*;
import com.foodbridge.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final SupershopRepository shops;
    private final RestaurantDonorRepository donors;
    private final CharityRepository charities;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    @Transactional
    public User registerConsumer(ConsumerRegisterRequest r) {
        ensureEmailFree(r.email());
        return users.save(User.builder().fullName(r.fullName()).email(r.email().toLowerCase())
                .passwordHash(encoder.encode(r.password())).phone(r.phone()).role(Role.CONSUMER).build());
    }

    @Transactional
    public User registerSupershop(PartnerRegisterRequest r) {
        ensureEmailFree(r.email());
        User u = users.save(User.builder().fullName(r.fullName()).email(r.email().toLowerCase())
                .passwordHash(encoder.encode(r.password())).phone(r.phone()).role(Role.SUPERSHOP_STAFF).build());
        shops.save(Supershop.builder().name(r.businessName()).address(r.address())
                .contactPerson(r.contactPerson()).latitude(r.latitude()).longitude(r.longitude())
                .verificationStatus(VerificationStatus.PENDING).createdAt(LocalDateTime.now()).owner(u).build());
        return u;
    }

    @Transactional
    public User registerDonor(PartnerRegisterRequest r) {
        ensureEmailFree(r.email());
        User u = users.save(User.builder().fullName(r.fullName()).email(r.email().toLowerCase())
                .passwordHash(encoder.encode(r.password())).phone(r.phone()).role(Role.DONOR_STAFF).build());
        donors.save(RestaurantDonor.builder().name(r.businessName()).address(r.address())
                .category(r.category()).contactPerson(r.contactPerson()).latitude(r.latitude())
                .longitude(r.longitude()).verificationStatus(VerificationStatus.PENDING)
                .createdAt(LocalDateTime.now()).owner(u).build());
        return u;
    }

    @Transactional
    public User registerCharity(CharityRegisterRequest r) {
        ensureEmailFree(r.email());
        User u = users.save(User.builder().fullName(r.fullName()).email(r.email().toLowerCase())
                .passwordHash(encoder.encode(r.password())).phone(r.phone())
                .role(Role.CHARITY_COORDINATOR).build());
        charities.save(Charity.builder().name(r.organisationName()).registrationDocument(r.registrationDocument())
                .address(r.address()).contactPerson(r.contactPerson()).latitude(r.latitude())
                .longitude(r.longitude()).verificationStatus(VerificationStatus.PENDING)
                .createdAt(LocalDateTime.now()).owner(u).build());
        return u;
    }

    public LoginResponse login(LoginRequest r) {
        User u = users.findByEmail(r.email().toLowerCase())
                .orElseThrow(() -> new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));
        if (!Boolean.TRUE.equals(u.getActive()) || !encoder.matches(r.password(), u.getPasswordHash()))
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        return new LoginResponse(jwt.generate(u.getEmail(), u.getRole().name()), u.getEmail(), u.getRole().name());
    }

    private void ensureEmailFree(String email) {
        if (users.existsByEmail(email.toLowerCase()))
            throw new ApiException("Email is already registered", HttpStatus.CONFLICT);
    }
}
