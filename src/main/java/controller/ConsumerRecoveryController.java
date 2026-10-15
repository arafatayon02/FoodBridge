package com.foodbridge.controller;
import com.foodbridge.entity.*;import com.foodbridge.entity.enums.Role;
import com.foodbridge.repository.*;
import lombok.RequiredArgsConstructor;import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.*;import org.springframework.mail.javamail.JavaMailSender;
import java.security.SecureRandom;import java.time.*;import java.util.*;
@RestController @RequiredArgsConstructor @RequestMapping("/api/auth/consumer-password")
public class ConsumerRecoveryController {
    private final UserRepository users;private final ConsumerResetCodeRepository codes;
    private final PasswordEncoder passwords;private final JavaMailSender emailService;
    private final SecureRandom random=new SecureRandom();
    public record Forgot(String email) {} public record Reset(String email,String code,String newPassword) {}
    @PostMapping("/forgot") public Map<String,String> forgot(@RequestBody Forgot request){
        String email=request.email()==null?"":request.email().trim().toLowerCase(Locale.ROOT);
        users.findByEmail(email).filter(u->u.getRole()==Role.CONSUMER).ifPresent(u->{
            String code=String.format("%06d",random.nextInt(1000000));
            ConsumerResetCode entity=new ConsumerResetCode();entity.setEmail(email);
            entity.setCodeHash(passwords.encode(code));entity.setExpiresAt(LocalDateTime.now().plusMinutes(10));codes.save(entity);
            SimpleMailMessage m=new SimpleMailMessage();m.setTo(email);m.setSubject("FoodBridge verification code");m.setText("Your 10-minute password reset code: "+code);emailService.send(m);
        });return Map.of("message","If an account exists, a reset email has been sent.");
    }
    @PostMapping("/reset") public Map<String,String> reset(@RequestBody Reset r){
        if(r.newPassword()==null||r.newPassword().length()<8)throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Password must have at least 8 characters");
        String email=r.email()==null?"":r.email().trim().toLowerCase(Locale.ROOT);
        ConsumerResetCode code=codes.findFirstByEmailAndUsedFalseOrderByIdDesc(email).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid code"));
        if(code.getExpiresAt().isBefore(LocalDateTime.now())||r.code()==null||!passwords.matches(r.code(),code.getCodeHash()))throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid or expired code");
        User u=users.findByEmail(email).orElseThrow();if(u.getRole()!=Role.CONSUMER)throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
        u.setPasswordHash(passwords.encode(r.newPassword()));users.save(u);code.setUsed(true);codes.save(code);return Map.of("message","Password updated");
    }
}

