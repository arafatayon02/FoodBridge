package com.foodbridge.backend.service;

import com.foodbridge.backend.dto.AddStaffRequest;
import com.foodbridge.backend.dto.RegisterSupershopRequest;
import com.foodbridge.backend.dto.VerifySupershopRequest;
import com.foodbridge.backend.entity.Role;
import com.foodbridge.backend.entity.Supershop;
import com.foodbridge.backend.entity.SupershopStatus;
import com.foodbridge.backend.entity.User;
import com.foodbridge.backend.exception.ApiException;
import com.foodbridge.backend.repository.SupershopRepository;
import com.foodbridge.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupershopService {

    private final SupershopRepository supershopRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Feature 1 + 4: Register a new supershop branch with status = PENDING,
     * and create its first (owner) staff login in the same transaction.
     */
    @Transactional
    public Supershop registerSupershop(RegisterSupershopRequest request) {
        if (userRepository.existsByEmail(request.getOwnerEmail())) {
            throw new ApiException("An account with this email already exists", HttpStatus.CONFLICT);
        }

        Supershop supershop = Supershop.builder()
                .businessName(request.getBusinessName())
                .address(request.getAddress())
                .contactPerson(request.getContactPerson())
                .contactPhone(request.getContactPhone())
                .status(SupershopStatus.PENDING)
                .build();

        supershop = supershopRepository.save(supershop);

        User owner = User.builder()
                .name(request.getOwnerName())
                .email(request.getOwnerEmail())
                .password(passwordEncoder.encode(request.getOwnerPassword()))
                .role(Role.SUPERSHOP_STAFF)
                .supershop(supershop)
                .isActive(true)
                .build();

        userRepository.save(owner);

        return supershop;
    }

    /**
     * Feature 5: Add another staff login under the same branch account.
     * Called by an already-authenticated staff member of that supershop.
     */
    @Transactional
    public User addStaff(Long supershopId, AddStaffRequest request, String requestingStaffEmail) {
        Supershop supershop = getSupershopOrThrow(supershopId);

        // Ensure the requester actually belongs to this supershop
        User requester = userRepository.findByEmail(requestingStaffEmail)
                .orElseThrow(() -> new ApiException("Requesting user not found", HttpStatus.UNAUTHORIZED));

        if (requester.getSupershop() == null || !requester.getSupershop().getId().equals(supershopId)) {
            throw new ApiException("You are not authorized to add staff to this branch", HttpStatus.FORBIDDEN);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("An account with this email already exists", HttpStatus.CONFLICT);
        }

        User staff = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SUPERSHOP_STAFF)
                .supershop(supershop)
                .isActive(true)
                .build();

        return userRepository.save(staff);
    }

    public List<User> listStaff(Long supershopId) {
        Supershop supershop = getSupershopOrThrow(supershopId);
        return userRepository.findBySupershopAndRole(supershop, Role.SUPERSHOP_STAFF);
    }

    /**
     * Feature 9: Admin approves or rejects a pending supershop.
     */
    @Transactional
    public Supershop verifySupershop(Long supershopId, VerifySupershopRequest request, String adminEmail) {
        Supershop supershop = getSupershopOrThrow(supershopId);

        if (supershop.getStatus() != SupershopStatus.PENDING) {
            throw new ApiException("This supershop has already been reviewed", HttpStatus.BAD_REQUEST);
        }

        boolean approve = Boolean.TRUE.equals(request.getApprove());

        if (!approve && (request.getRejectionReason() == null || request.getRejectionReason().isBlank())) {
            throw new ApiException("A rejection reason is required when rejecting", HttpStatus.BAD_REQUEST);
        }

        supershop.setStatus(approve ? SupershopStatus.APPROVED : SupershopStatus.REJECTED);
        supershop.setReviewedBy(adminEmail);
        supershop.setReviewedAt(LocalDateTime.now());
        supershop.setRejectionReason(approve ? null : request.getRejectionReason());

        supershop = supershopRepository.save(supershop);

        // Notify the branch owner of the outcome
        List<User> staff = userRepository.findBySupershopAndRole(supershop, Role.SUPERSHOP_STAFF);
        if (!staff.isEmpty()) {
            emailService.sendVerificationResult(
                    staff.get(0).getEmail(), approve, supershop.getRejectionReason());
        }

        return supershop;
    }

    public List<Supershop> listPending() {
        return supershopRepository.findByStatus(SupershopStatus.PENDING);
    }

    public Supershop getSupershopOrThrow(Long id) {
        return supershopRepository.findById(id)
                .orElseThrow(() -> new ApiException("Supershop not found", HttpStatus.NOT_FOUND));
    }
}
