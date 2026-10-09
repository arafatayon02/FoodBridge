package com.foodbridge.controller;
import com.foodbridge.entity.*;import com.foodbridge.entity.enums.Role;
import com.foodbridge.repository.*;import lombok.RequiredArgsConstructor;
import org.springframework.http.*;import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;import java.time.*;import java.util.*;
@RestController @RequiredArgsConstructor @RequestMapping("/api/consumer/discounts")
public class ConsumerDiscountController {
    private final DiscountOfferRepository discounts;private final UserRepository users;
    @GetMapping public List<DiscountOffer> feed(Authentication auth){
        if(auth==null)throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED);
        User user=users.findByEmail(auth.getName()).orElseThrow();
        if(user.getRole()!=Role.CONSUMER)throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
        return discounts.findByActiveTrueAndExpiresAtAfterOrderByExpiresAtAsc(LocalDateTime.now());
    }
}
