package com.foodbridge.donation;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;
public record DonationListingRequest(@NotBlank @Size(max=160) String foodName,
                                     @NotBlank @Size(max=2000) String description,
                                     @NotBlank @Size(max=120) String donorOrganization,
                                     @NotBlank @Size(max=300) String pickupAddress,
                                     @Min(1) @Max(100000) int quantity,
                                     @NotNull @Future LocalDateTime pickupStart,
                                     @NotNull @Future LocalDateTime safetyCutoff,
                                     Set<DietaryTag> dietaryTags) {}
