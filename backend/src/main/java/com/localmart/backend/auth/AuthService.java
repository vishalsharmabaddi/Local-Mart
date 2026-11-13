package com.localmart.backend.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localmart.backend.common.BadRequestException;
import com.localmart.backend.security.JwtService;
import com.localmart.backend.user.Role;
import com.localmart.backend.user.User;
import com.localmart.backend.user.UserRepository;
import com.localmart.backend.user.UserResponse;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByPhone(request.phone())
                .orElseThrow(InvalidCredentialsException::new);           // phone not found

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();                       // wrong password, SAME error
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, "Bearer", jwtService.getExpirySeconds(), UserResponse.from(user));
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (request.role() == Role.ADMIN) {
            throw new BadRequestException("Cannot register as ADMIN");   // nobody makes themselves admin
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new PhoneAlreadyRegisteredException(request.phone());
        }

        String hashedPassword = passwordEncoder.encode(request.password());   // "secret123" → "$2a$10$..."
        User user = new User(request.name(), request.phone(), hashedPassword, request.role());

        return UserResponse.from(userRepository.save(user));
    }
}
