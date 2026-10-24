package com.foodbridge.orders;
import org.springframework.stereotype.Controller;import org.springframework.ui.Model;import org.springframework.web.bind.annotation.*;
@Controller public class PickupVerificationController {private final OrderService service;public PickupVerificationController(OrderService s){service=s;}
    @GetMapping("/staff/pickup-verification") public String form(){return "orders/pickup-verification";}
    @PostMapping("/staff/pickup-verification") public String verify(@RequestParam String pickupCode,Model model){try{PurchaseOrder o=service.verifyPickupCode(pickupCode);model.addAttribute("order",o);model.addAttribute("message","Pickup verified successfully. Order marked as picked up.");}catch(RuntimeException e){model.addAttribute("error",e.getMessage());}return "orders/pickup-verification";}
}
