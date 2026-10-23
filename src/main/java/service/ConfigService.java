package com.foodrescue.consumer.service;

import com.foodrescue.consumer.exception.NotFoundException;
import com.foodrescue.consumer.model.ConfigAudit;
import com.foodrescue.consumer.model.SystemConfig;
import com.foodrescue.consumer.model.SystemConfig.ValueType;
import com.foodrescue.consumer.repository.ConfigAuditRepository;
import com.foodrescue.consumer.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** FOOD-89: Build Global System Configuration - typed, validated, audited runtime settings. */
@Service
@RequiredArgsConstructor
public class ConfigService {

    public static final String KG_PER_MEAL = "impact.kg_per_meal";
    public static final String CO2_PER_KG = "impact.co2_kg_per_kg";
    public static final String DISPUTE_WINDOW_DAYS = "dispute.window_days";
    public static final String INSPECTION_VALIDITY_DAYS = "inspection.validity_days";
    public static final String MAX_QTY_PER_ORDER = "order.max_quantity_per_order";
    public static final String MAINTENANCE_MODE = "platform.maintenance_mode";
    public static final String COMMISSION_PERCENT = "platform.commission_percent";
    public static final String AUTO_FLAG_THRESHOLD = "moderation.auto_flag_report_threshold";
    public static final String PICKUP_GUIDELINES = "pickup.guidelines";

    private final SystemConfigRepository repo;
    private final ConfigAuditRepository auditRepo;

    private record Def(String key, String value, ValueType type, String desc) {}

    private static final List<Def> DEFAULTS = List.of(
            new Def(KG_PER_MEAL, "0.4", ValueType.DECIMAL, "Average kg of food per rescued meal"),
            new Def(CO2_PER_KG, "2.5", ValueType.DECIMAL, "kg CO2e avoided per kg of food rescued"),
            new Def(DISPUTE_WINDOW_DAYS, "3", ValueType.INT, "Days after completion a consumer may open a dispute"),
            new Def(INSPECTION_VALIDITY_DAYS, "365", ValueType.INT, "Days an inspection stays valid"),
            new Def(MAX_QTY_PER_ORDER, "10", ValueType.INT, "Maximum quantity per order"),
            new Def(MAINTENANCE_MODE, "false", ValueType.BOOLEAN, "Block new orders while true"),
            new Def(COMMISSION_PERCENT, "5", ValueType.DECIMAL, "Platform commission percent"),
            new Def(AUTO_FLAG_THRESHOLD, "3", ValueType.INT, "Consumer reports before a listing is auto-flagged"),
            new Def(PICKUP_GUIDELINES, "Bring ID, check the item on pickup, and consume the same day.",
                    ValueType.STRING, "Guidelines shown on store safety page"));

    @Transactional
    public void ensureDefaults() {
        for (Def d : DEFAULTS) {
            if (!repo.existsById(d.key())) {
                SystemConfig c = new SystemConfig();
                c.setConfigKey(d.key());
                c.setConfigValue(d.value());
                c.setValueType(d.type());
                c.setDescription(d.desc());
                c.setUpdatedBy("system");
                repo.save(c);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<SystemConfig> all() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public SystemConfig get(String key) {
        return repo.findById(key).orElseThrow(() -> new NotFoundException("Config key '" + key + "' not found"));
    }

    @Transactional
    public SystemConfig update(String key, String value, String updatedBy) {
        SystemConfig c = get(key);
        validate(c.getValueType(), value);
        ConfigAudit a = new ConfigAudit();
        a.setConfigKey(key);
        a.setOldValue(c.getConfigValue());
        a.setNewValue(value);
        a.setChangedBy(updatedBy);
        auditRepo.save(a);
        c.setConfigValue(value);
        c.setUpdatedBy(updatedBy);
        c.setUpdatedAt(LocalDateTime.now());
        return repo.save(c);
    }

    @Transactional(readOnly = true)
    public List<ConfigAudit> history(String key) {
        return auditRepo.findByConfigKeyOrderByChangedAtDesc(key);
    }

    @Transactional(readOnly = true)
    public List<ConfigAudit> recentAudit() {
        return auditRepo.findTop100ByOrderByChangedAtDesc();
    }

    @Transactional(readOnly = true)
    public int getInt(String key) {
        return Integer.parseInt(get(key).getConfigValue().trim());
    }

    @Transactional(readOnly = true)
    public BigDecimal getDecimal(String key) {
        return new BigDecimal(get(key).getConfigValue().trim());
    }

    @Transactional(readOnly = true)
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key).getConfigValue().trim());
    }

    @Transactional(readOnly = true)
    public String getString(String key) {
        return get(key).getConfigValue();
    }

    private void validate(ValueType type, String value) {
        try {
            switch (type) {
                case INT -> {
                    if (Integer.parseInt(value.trim()) < 0) throw new IllegalArgumentException("must be >= 0");
                }
                case DECIMAL -> {
                    if (new BigDecimal(value.trim()).signum() < 0) throw new IllegalArgumentException("must be >= 0");
                }
                case BOOLEAN -> {
                    String v = value.trim().toLowerCase();
                    if (!v.equals("true") && !v.equals("false")) throw new IllegalArgumentException("must be true/false");
                }
                case STRING -> { }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Value '" + value + "' is not a valid " + type);
        }
    }
}
