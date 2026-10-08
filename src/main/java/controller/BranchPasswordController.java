package com.foodbridge.backend.controller;
import com.foodbridge.backend.entity.*;
import com.foodbridge.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.security.*;import java.time.*;import java.util.*;
@RestController @RequiredArgsConstructor @RequestMapping("/api/branch-password")
public class BranchPasswordController {
    private final UserRepository users; private final BranchPasswordResetRepository resets;
    private final PasswordEncoder passwords; private final JavaMailSender mail;
    private final SecureRandom random = new SecureRandom();
    public record Forgot(String email) {} public record Reset(String email,String code,String newPassword) {}
    @PostMapping("/forgot") public Map<String,String> forgot(@RequestBody Forgot r){
        String email=r.email()==null?"":r.email().trim().toLowerCase(Locale.ROOT);
        users.findByEmail(email).filter(u->u.getRole()==Role.SUPERSHOP_STAFF).ifPresent(u->{
            String code=String.format("%06d",random.nextInt(1000000));
            BranchPasswordReset reset=new BranchPasswordReset(); reset.email=email;
            reset.tokenHash=passwords.encode(code); reset.expiresAt=Instant.now().plusSeconds(600);
            resets.save(reset); SimpleMailMessage m=new SimpleMailMessage();m.setTo(email);
            m.setSubject("FoodBridge password reset");m.setText("Your 10-minute reset code: "+code);mail.send(m);
        });
        return Map.of("message","If the account exists, a reset code has been sent.");
    }
    @PostMapping("/reset") public Map<String,String> reset(@RequestBody Reset r){
        if(r.newPassword()==null || r.newPassword().length()<8) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Password too short");
        String email=r.email()==null?"":r.email().trim().toLowerCase(Locale.ROOT);
        var token=resets.findFirstByEmailAndUsedFalseOrderByIdDesc(email).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid code"));
        if(token.expiresAt.isBefore(Instant.now()) || !passwords.matches(r.code(),token.tokenHash))
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid or expired code");
        var u=users.findByEmail(email).orElseThrow(); u.setPassword(passwords.encode(r.newPassword()));users.save(u);
        token.used=true; resets.save(token);return Map.of("message","Password updated");
    }
}
