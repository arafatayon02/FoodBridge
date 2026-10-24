package com.foodbridge.donation;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.Set;

@Controller
public class DonationPageController {
    private final DonationListingService service;
    public DonationPageController(DonationListingService service){this.service=service;}
    @GetMapping("/donations") public String donations(Model model){model.addAttribute("donations",service.available());return "donation/list";}
    @GetMapping("/donor/donations/new") public String form(Model model){model.addAttribute("form",new DonationForm());model.addAttribute("tags",DietaryTag.values());return "donation/form";}
    @PostMapping("/donor/donations") public String create(@Valid @ModelAttribute("form") DonationForm form,BindingResult errors,Model model,Authentication auth,RedirectAttributes flash){if(errors.hasErrors()||!form.getPickupStart().isBefore(form.getSafetyCutoff())){if(!errors.hasErrors())model.addAttribute("cutoffError","Pickup start must be before the safety cutoff.");model.addAttribute("tags",DietaryTag.values());return "donation/form";}service.create(form.toRequest(),Math.abs((long)auth.getName().hashCode()));flash.addFlashAttribute("success","Donation listing published.");return "redirect:/donations";}
}
