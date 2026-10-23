package com.foodrescue.consumer.controller;

import com.foodrescue.consumer.dto.ConfigUpdateRequest;
import com.foodrescue.consumer.model.ConfigAudit;
import com.foodrescue.consumer.model.SystemConfig;
import com.foodrescue.consumer.service.ConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** FOOD-89 */
@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService config;

    @GetMapping
    public List<SystemConfig> all() {
        return config.all();
    }

    @GetMapping("/audit")
    public List<ConfigAudit> audit() {
        return config.recentAudit();
    }

    @GetMapping("/{key}")
    public SystemConfig get(@PathVariable String key) {
        return config.get(key);
    }

    @PutMapping("/{key}")
    public SystemConfig update(@PathVariable String key, @Valid @RequestBody ConfigUpdateRequest r) {
        return config.update(key, r.value(), r.updatedBy());
    }

    @GetMapping("/{key}/history")
    public List<ConfigAudit> history(@PathVariable String key) {
        return config.history(key);
    }
}
