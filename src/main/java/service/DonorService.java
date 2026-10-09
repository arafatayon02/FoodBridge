package com.foodbridge.backend.service;

import com.foodbridge.backend.dto.AddStaffRequest;
import com.foodbridge.backend.dto.RegisterDonorRequest;
import com.foodbridge.backend.dto.VerifyDonorRequest;
import com.foodbridge.backend.entity.DonorStatus;
import com.foodbridge.backend.entity.RestaurantDonor;
import com.foodbridge.backend.entity.Role;
import com.foodbridge.backend.entity.User;
import com.foodbridge.backend.exception.ApiException;
import com.foodbridge.backend.repository.DonorRepository;
import com.foodbridge.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mirrors SupershopService but for the Donation Track (restaurants/bakeries).
 * FOOD-58: Restaurant/Bakery Registration & Staff Management (parent)
 * FOOD-59: Build Restaurant/Bakery Registration
 * FOOD-60: Implement Donor Approval Status
 */
@Service
@RequiredArgsConstructor
public class DonorService {

    private final DonorRepository donorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // FOOD-59: register a restaurant/bakery with status = PENDING,
    // plus its first (owner) staff login.
    @Transactional
    public RestaurantDonor registerDonor(RegisterDonorRequest request) {
        if (userRepository.existsByEmail(request.getOwnerEmail())) {
            throw new ApiException("An account with this email already exists", HttpStatus.CONFLICT);
        }

        RestaurantDonor donor = RestaurantDonor.builder()
                .businessName(request.getBusinessName())
                .address(request.getAddress())
                .category(request.getCategory())
                .contactPerson(request.getContactPerson())
                .contactPhone(request.getContactPhone())
                .status(DonorStatus.PENDING)
                .build();

        donor = donorRepository.save(donor);

        User owner = User.builder()
                .name(request.getOwnerName())
                .email(request.getOwnerEmail())
                .password(passwordEncoder.encode(request.getOwnerPassword()))
                .role(Role.DONOR_STAFF)
                .restaurantDonor(donor)
                .isActive(true)
                .build();

        userRepository.save(owner);

        return donor;
    }

    // FOOD-58: add another staff login under the same restaurant/bakery account
    @Transactional
    public User addStaff(Long donorId, AddStaffRequest request, String requestingStaffEmail) {
        RestaurantDonor donor = getDonorOrThrow(donorId);

        User requester = userRepository.findByEmail(requestingStaffEmail)
                .orElseThrow(() -> new ApiException("Requesting user not found", HttpStatus.UNAUTHORIZED));

        if (requester.getRestaurantDonor() == null
                || !requester.getRestaurantDonor().getId().equals(donorId)) {
            throw new ApiException("You are not authorized to add staff to this business", HttpStatus.FORBIDDEN);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("An account with this email already exists", HttpStatus.CONFLICT);
        }

        User staff = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.DONOR_STAFF)
                .restaurantDonor(donor)
                .isActive(true)
                .build();

        return userRepository.save(staff);
    }

    public List<User> listStaff(Long donorId) {
        RestaurantDonor donor = getDonorOrThrow(donorId);
        return userRepository.findByRestaurantDonorAndRole(donor, Role.DONOR_STAFF);
    }

    // FOOD-60: admin approves or rejects a pending restaurant/bakery
    @Transactional
    public RestaurantDonor verifyDonor(Long donorId, VerifyDonorRequest request, String adminEmail) {
        RestaurantDonor donor = getDonorOrThrow(donorId);

        if (donor.getStatus() != DonorStatus.PENDING) {
            throw new ApiException("This business has already been reviewed", HttpStatus.BAD_REQUEST);
        }

        boolean approve = Boolean.TRUE.equals(request.getApprove());

        if (!approve && (request.getRejectionReason() == null || request.getRejectionReason().isBlank())) {
            throw new ApiException("A rejection reason is required when rejecting", HttpStatus.BAD_REQUEST);
        }

        donor.setStatus(approve ? DonorStatus.APPROVED : DonorStatus.REJECTED);
        donor.setReviewedBy(adminEmail);
        donor.setReviewedAt(LocalDateTime.now());
        donor.setRejectionReason(approve ? null : request.getRejectionReason());

        donor = donorRepository.save(donor);

        List<User> staff = userRepository.findByRestaurantDonorAndRole(donor, Role.DONOR_STAFF);
        if (!staff.isEmpty()) {
            emailService.sendVerificationResult(
                    staff.get(0).getEmail(), approve, donor.getRejectionReason());
        }

        return donor;
    }

    // FOOD-60: list businesses awaiting a decision (their "approval status" view)
    public List<RestaurantDonor> listPending() {
        return donorRepository.findByStatus(DonorStatus.PENDING);
    }

    public RestaurantDonor getDonorOrThrow(Long id) {
        return donorRepository.findById(id)
                .orElseThrow(() -> new ApiException("Restaurant/bakery not found", HttpStatus.NOT_FOUND));
    }
}
