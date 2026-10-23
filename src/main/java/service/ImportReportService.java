package com.foodrescue.consumer.service;

import com.foodrescue.consumer.dto.ImpactReport;
import com.foodrescue.consumer.dto.ImpactReport.MonthlyImpact;
import com.foodrescue.consumer.dto.ImpactReport.StoreImpact;
import com.foodrescue.consumer.model.CustomerOrder;
import com.foodrescue.consumer.model.Store;
import com.foodrescue.consumer.repository.OrderRepository;
import com.foodrescue.consumer.repository.StoreRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/** FOOD-87: Build CSR & Impact Reports. Counts COMPLETED orders only. */
@Service
@RequiredArgsConstructor
public class ImpactReportService {

    private final OrderRepository orders;
    private final StoreRepository stores;
    private final ConfigService config;

    @Transactional(readOnly = true)
    public ImpactReport generate(LocalDate from, LocalDate to, Long storeId) {
        LocalDate end = to != null ? to : LocalDate.now();
        LocalDate start = from != null ? from : end.minusMonths(12);
        if (start.isAfter(end)) throw new IllegalArgumentException("'from' must not be after 'to'");

        Specification<CustomerOrder> spec = (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.equal(root.get("status"), CustomerOrder.Status.COMPLETED));
            ps.add(cb.between(root.get("createdAt"), start.atStartOfDay(), end.atTime(LocalTime.MAX)));
            if (storeId != null) ps.add(cb.equal(root.get("storeId"), storeId));
            return cb.and(ps.toArray(new Predicate[0]));
        };
        List<CustomerOrder> list = orders.findAll(spec);

        BigDecimal kgPerMeal = config.getDecimal(ConfigService.KG_PER_MEAL);
        BigDecimal co2PerKg = config.getDecimal(ConfigService.CO2_PER_KG);

        long meals = list.stream().mapToLong(CustomerOrder::getQuantity).sum();
        BigDecimal foodKg = kg(meals, kgPerMeal);
        BigDecimal saved = list.stream().map(o -> o.getOriginalTotal().subtract(o.getPaidTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal revenue = list.stream().map(CustomerOrder::getPaidTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, String> names = stores.findAllById(
                        list.stream().map(CustomerOrder::getStoreId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Store::getId, Store::getName));

        List<StoreImpact> byStore = list.stream().collect(Collectors.groupingBy(CustomerOrder::getStoreId)).entrySet()
                .stream().map(e -> {
                    long m = e.getValue().stream().mapToLong(CustomerOrder::getQuantity).sum();
                    return new StoreImpact(e.getKey(), names.getOrDefault(e.getKey(), "Store " + e.getKey()),
                            e.getValue().size(), m, kg(m, kgPerMeal));
                }).sorted(Comparator.comparingLong(StoreImpact::meals).reversed()).toList();

        Map<YearMonth, List<CustomerOrder>> grouped = list.stream()
                .collect(Collectors.groupingBy(o -> YearMonth.from(o.getCreatedAt()), TreeMap::new, Collectors.toList()));
        List<MonthlyImpact> byMonth = grouped.entrySet().stream().map(e -> {
            long m = e.getValue().stream().mapToLong(CustomerOrder::getQuantity).sum();
            BigDecimal kg = kg(m, kgPerMeal);
            BigDecimal ms = e.getValue().stream().map(o -> o.getOriginalTotal().subtract(o.getPaidTotal()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return new MonthlyImpact(e.getKey().toString(), e.getValue().size(), m, kg,
                    kg.multiply(co2PerKg).setScale(2, RoundingMode.HALF_UP), ms);
        }).toList();

        return new ImpactReport(start, end, list.size(), meals, foodKg,
                foodKg.multiply(co2PerKg).setScale(2, RoundingMode.HALF_UP), saved, revenue, byStore, byMonth);
    }

    public String toCsv(ImpactReport r) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Impact report ").append(r.from()).append(" to ").append(r.to()).append('\n');
        sb.append("total_orders,meals_rescued,food_saved_kg,co2_avoided_kg,money_saved,revenue_recovered\n");
        sb.append(r.totalOrders()).append(',').append(r.mealsRescued()).append(',').append(r.foodSavedKg()).append(',')
                .append(r.co2AvoidedKg()).append(',').append(r.moneySavedByConsumers()).append(',')
                .append(r.revenueRecovered()).append("\n\n");
        sb.append("month,orders,meals,food_saved_kg,co2_avoided_kg,money_saved\n");
        r.byMonth().forEach(m -> sb.append(m.month()).append(',').append(m.orders()).append(',').append(m.meals())
                .append(',').append(m.foodSavedKg()).append(',').append(m.co2AvoidedKg()).append(',')
                .append(m.moneySaved()).append('\n'));
        sb.append("\nstore_id,store_name,orders,meals,food_saved_kg\n");
        r.byStore().forEach(s -> sb.append(s.storeId()).append(",\"").append(s.storeName().replace("\"", "\"\""))
                .append("\",").append(s.orders()).append(',').append(s.meals()).append(',')
                .append(s.foodSavedKg()).append('\n'));
        return sb.toString();
    }

    private BigDecimal kg(long meals, BigDecimal kgPerMeal) {
        return kgPerMeal.multiply(BigDecimal.valueOf(meals)).setScale(2, RoundingMode.HALF_UP);
    }
}
