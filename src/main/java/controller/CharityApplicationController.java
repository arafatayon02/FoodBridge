package com.foodbridge.backend.controller;
import com.foodbridge.backend.entity.CharityApplication;
import com.foodbridge.backend.repository.CharityApplicationRepository;
import lombok.RequiredArgsConstructor;import jakarta.validation.Valid;import jakarta.validation.constraints.*;
import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequiredArgsConstructor @RequestMapping("/api/charity-applications")
public class CharityApplicationController {
    private final CharityApplicationRepository repo;
    public record Register(@NotBlank String name,@Email @NotBlank String email,@NotBlank String address,@NotBlank String registrationNumber){}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public CharityApplication register(@Valid @RequestBody Register r){
        String email=r.email().trim().toLowerCase(Locale.ROOT);
        if(repo.existsByEmail(email))throw new org.springframework.web.server.ResponseStatusException(HttpStatus.CONFLICT,"Already registered");
        CharityApplication c=new CharityApplication();c.setName(r.name());c.setEmail(email);c.setAddress(r.address());c.setRegistrationNumber(r.registrationNumber());return repo.save(c);
    }
    @GetMapping("/admin/pending") public List<CharityApplication> pending(){return repo.findByStatus(CharityApplication.Status.PENDING);}
    @PutMapping("/admin/{id}/decision") public CharityApplication decide(@PathVariable Long id,@RequestParam boolean approve){
        var c=repo.findById(id).orElseThrow();if(c.getStatus()!=CharityApplication.Status.PENDING)throw new org.springframework.web.server.ResponseStatusException(HttpStatus.CONFLICT);
        c.setStatus(approve?CharityApplication.Status.APPROVED:CharityApplication.Status.REJECTED);return repo.save(c);
    }
}
