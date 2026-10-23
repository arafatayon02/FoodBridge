package com.foodrescue.consumer.controller;

import com.foodrescue.consumer.dto.DisputeRequest;
import com.foodrescue.consumer.dto.DisputeResolution;
import com.foodrescue.consumer.model.Dispute;
import com.foodrescue.consumer.service.DisputeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** FOOD-88 */
@RestController
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService disputes;

    @PostMapping("/api/disputes")
    @ResponseStatus(HttpStatus.CREATED)
    public Dispute open(@Valid @RequestBody DisputeRequest r) {
        return disputes.open(r);
    }

    @GetMapping("/api/consumers/{consumerId}/disputes")
    public List<Dispute> mine(@PathVariable Long consumerId) {
        return disputes.forConsumer(consumerId);
    }

    @GetMapping("/api/admin/disputes")
    public Page<Dispute> queue(@RequestParam(required = false) Dispute.Status status,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {
        return disputes.queue(status, page, size);
    }

    @GetMapping("/api/admin/disputes/{id}")
    public Dispute get(@PathVariable Long id) {
        return disputes.get(id);
    }

    @PostMapping("/api/admin/disputes/{id}/review")
    public Dispute review(@PathVariable Long id) {
        return disputes.startReview(id);
    }

    @PostMapping("/api/admin/disputes/{id}/resolve")
    public Dispute resolve(@PathVariable Long id, @Valid @RequestBody DisputeResolution r) {
        return disputes.resolve(id, r);
    }
}
