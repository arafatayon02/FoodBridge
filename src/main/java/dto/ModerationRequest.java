package com.foodrescue.consumer.dto;

import jakarta.validation.constraints.NotBlank;

public record ModerationRequest(@NotBlank String moderator, String reason) {
}
