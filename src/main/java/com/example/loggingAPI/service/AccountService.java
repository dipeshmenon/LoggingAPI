package com.example.loggingAPI.service;

import com.example.loggingAPI.model.Account;

import com.example.loggingAPI.repository.AccountRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public double getBalance(String username) {
        Account account = accountRepository.findByUsersUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return account.getBalance();
    }
    @CircuitBreaker(name = "depositCB", fallbackMethod = "depositFallback")
    public void deposit(String username, double amount) {
        Account account = accountRepository.findByUsersUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
    }

    public void withdraw(String username, double amount) {
        Account account = accountRepository.findByUsersUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
    }
    public ResponseEntity<String> depositFallback() {
        return ResponseEntity.status(503).body("Deposit service unavailable");
    }
}
