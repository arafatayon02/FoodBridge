package com.foodbridge.orders;
import com.foodbridge.listing.RetailListingRepository;import org.springframework.security.core.Authentication;import org.springframework.stereotype.Controller;import org.springframework.ui.Model;import org.springframework.web.bind.annotation.*;
@Controller public class CashOnPickupController {private final RetailListingRepository listings;private final OrderService service;public CashOnPickupController(RetailListingRepository l,OrderService s){listings=l;service=s;}
    @GetMapping("/products/{id}/cash-on-pickup") public String form(@PathVariable Long id,Model m){m.addAttribute("listing",listings.findById(id).orElseThrow());return "orders/cash-on-pickup";}
    @PostMapping("/products/{id}/cash-on-pickup") public String create(@PathVariable Long id,@RequestParam(defaultValue="1") int quantity,Authentication auth){PurchaseOrder o=service.reserveCashOnPickup(id,quantity,auth.getName());return "redirect:/orders/"+o.getId();}
}
