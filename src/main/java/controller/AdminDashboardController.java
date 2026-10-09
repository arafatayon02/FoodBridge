package com.foodbridge.backend.controller;
import com.foodbridge.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequiredArgsConstructor @RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {
    private final SupershopService shops; private final DonorService donors;
    @GetMapping("/summary") public Map<String,Integer> summary(){
        return Map.of("pendingSupershops",shops.listPending().size(),"pendingDonors",donors.listPending().size());
    }
}
