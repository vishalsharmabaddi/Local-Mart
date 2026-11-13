package com.localmart.backend.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.localmart.backend.common.BadRequestException;
import com.localmart.backend.security.JwtService;
import com.localmart.backend.user.Role;
import com.localmart.backend.user.User;
import com.localmart.backend.user.UserRepository;
import com.localmart.backend.user.UserResponse;

class AuthServiceTest {

    UserRepository userRepository;
    JwtService jwtService;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();   // REAL encoder, fake repository
    AuthService authService;

    @BeforeEach   // runs before every test → fresh objects each time
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtService = mock(JwtService.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    // ---------- login ----------

    @Test
    void login_withCorrectPassword_returnsToken() {
        User ramesh = new User("Ramesh", "9812345678", passwordEncoder.encode("secret123"), Role.SHOP_OWNER);
        when(userRepository.findByPhone("9812345678")).thenReturn(Optional.of(ramesh));
        when(jwtService.generateToken(ramesh)).thenReturn("fake.jwt.token");

        LoginResponse response = authService.login(new LoginRequest("9812345678", "secret123"));

        assertEquals("fake.jwt.token", response.token());
        assertEquals("Bearer", response.tokenType());
    }

    @Test
    void login_withWrongPassword_throwsInvalidCredentials() {
        User ramesh = new User("Ramesh", "9812345678", passwordEncoder.encode("secret123"), Role.SHOP_OWNER);
        when(userRepository.findByPhone("9812345678")).thenReturn(Optional.of(ramesh));

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("9812345678", "WRONG-pass")));

        verify(jwtService, never()).generateToken(any(User.class));   // no token for wrong password
    }

    @Test
    void login_withUnknownPhone_throwsSameInvalidCredentials() {
        when(userRepository.findByPhone("9000000000")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("9000000000", "secret123")));
    }

    // ---------- register ----------

    @Test
    void register_savesHashedPassword_neverPlainText() {
        when(userRepository.existsByPhone("9812345678")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.register(new RegisterRequest("Ramesh", "9812345678", "secret123", Role.CUSTOMER));

        // catch the User object that the service passed to save()
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertNotEquals("secret123", savedUser.getPassword());                     // not plain text
        assertTrue(passwordEncoder.matches("secret123", savedUser.getPassword()));  // but it's the right hash
    }

    @Test
    void register_returnsUserWithoutPassword() {
        when(userRepository.existsByPhone("9812345678")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = authService.register(
                new RegisterRequest("Ramesh", "9812345678", "secret123", Role.SHOP_OWNER));

        assertEquals("Ramesh", response.name());
        assertEquals(Role.SHOP_OWNER, response.role());
    }

    @Test
    void register_whenPhoneTaken_throwsConflict() {
        when(userRepository.existsByPhone("9812345678")).thenReturn(true);

        assertThrows(PhoneAlreadyRegisteredException.class, () -> authService.register(
                new RegisterRequest("Suresh", "9812345678", "secret123", Role.CUSTOMER)));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_asAdmin_isRejected() {
        assertThrows(BadRequestException.class, () -> authService.register(
                new RegisterRequest("Hacker", "9000000000", "secret123", Role.ADMIN)));

        verify(userRepository, never()).save(any(User.class));
    }
}
