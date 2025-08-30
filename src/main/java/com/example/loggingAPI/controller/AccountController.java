package com.example.loggingAPI.controller;

import com.example.loggingAPI.service.AccountService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance(Authentication auth) {
        String username = auth.getName();
        double balance = accountService.getBalance(username);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestParam double amount, Authentication auth) {
        String username = auth.getName();
        accountService.deposit(username, amount);
        log.info("Deposit: {} | User: {}", amount, username);
        return ResponseEntity.ok("Deposited successfully");
    }

    @PostMapping("/withdraw")
    @RateLimiter(name = "withdrawRateLimiter", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<String> withdraw(@RequestParam double amount, Authentication auth) {
        String username = auth.getName();
        accountService.withdraw(username, amount);
        log.info("Withdraw: {} | User: {}", amount, username);
        return ResponseEntity.ok("Withdrawn successfully");
    }

    public ResponseEntity<String> rateLimitFallback(double amount, Authentication auth, RequestNotPermitted ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many withdrawals. Try again later.");
    }
}
