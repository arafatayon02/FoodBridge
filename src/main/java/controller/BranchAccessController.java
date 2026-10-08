package com.foodbridge.backend.controller;
import com.foodbridge.backend.entity.*;
import com.foodbridge.backend.repository.UserRepository;
import com.foodbridge.backend.service.SupershopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequiredArgsConstructor @RequestMapping("/api/branches")
public class BranchAccessController {
    private final UserRepository users; private final SupershopService shops;
    private User member(Long id, Authentication auth) {
        if(auth==null) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED);
        User u=users.findByEmail(auth.getName()).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if(u.getRole()!=Role.SUPERSHOP_STAFF || u.getSupershop()==null || !id.equals(u.getSupershop().getId()))
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN,"Wrong branch");
        if(shops.getSupershopOrThrow(id).getStatus()!=SupershopStatus.APPROVED)
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN,"Branch pending approval");
        return u;
    }
    @GetMapping("/{id}/me") public Map<String,Object> me(@PathVariable Long id,Authentication a){
        User u=member(id,a); return Map.of("branchId",id,"email",u.getEmail(),"role",u.getRole().name());
    }
    @GetMapping("/{id}/staff") public List<Map<String,Object>> staff(@PathVariable Long id,Authentication a){
        member(id,a); return shops.listStaff(id).stream().map(u->Map.<String,Object>of("email",u.getEmail(),"name",u.getName())).toList();
    }
}
