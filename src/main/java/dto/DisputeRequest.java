package com.foodrescue.consumer.dto;

import com.foodrescue.consumer.model.Dispute;
import jakarta.validation.constraints.NotNull;

public record DisputeRequest(
        @NotNull Long orderId,
        @NotNull Long consumerId,
        @NotNull Dispute.Reason reason,
        String description) {
}
