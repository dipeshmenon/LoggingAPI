package com.example.loggingAPI.service;

import com.example.loggingAPI.model.Account;
import com.example.loggingAPI.model.Users;
import com.example.loggingAPI.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Users register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        Users users = new Users();
        users.setUsername(username);
        users.setPassword(passwordEncoder.encode(password));

        Account account = new Account();
        account.setBalance(0.0);
        account.setUsers(users);
        users.setAccount(account);

        return userRepository.save(users);
    }

    public Users authenticate(String username, String password) {
        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, users.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return users;
    }
}
