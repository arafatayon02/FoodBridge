package com.foodbridge.orders;
import org.springframework.security.core.Authentication;import org.springframework.stereotype.Controller;import org.springframework.ui.Model;import org.springframework.web.bind.annotation.*;
@Controller public class OrderController {private final PurchaseOrderRepository orders;private final OrderService service;public OrderController(PurchaseOrderRepository o,OrderService s){orders=o;service=s;}
    @GetMapping("/consumer/orders") public String history(Authentication auth,Model m){m.addAttribute("orders",orders.findByConsumerEmailOrderByCreatedAtDesc(auth.getName()));return "orders/history";}
    @PostMapping("/staff/orders/{id}/status") public String status(@PathVariable Long id,@RequestParam OrderStatus status){service.changeStatus(id,status);return "redirect:/consumer/orders";}
}

