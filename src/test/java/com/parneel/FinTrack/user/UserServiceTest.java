package com.parneel.FinTrack.user;

import com.parneel.FinTrack.config.JwtUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_shouldSaveUserSuccessfully(){

        String username = "parneel";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        userService.registerUser(username, rawPassword);

        verify(userRepository).findByUsername(username);
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void registerUser_shouldThrowException_whenUsernameAlreadyExists(){
        String username = "parneel";
        String rawPassword = "password123";

        User existingUser = new User();
        existingUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.registerUser(username, rawPassword));

        assertEquals("Username already exists", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_shouldReturnToken_whenCredentialsAreCorrect(){
        String username = "parneel";
        String password = "password123";
        Role role = Role.USER;

        User user = new User();

        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        when(jwtUtil.generateToken(username, user.getRole().toString())).thenReturn("dummy-jwt-token");
        String token = userService.loginUser(username, password);
        assertEquals("dummy-jwt-token", token);

        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(password, user.getPassword());
        verify(jwtUtil).generateToken(username, user.getRole().toString());
    }

    @Test
    void loginUser_shouldThrowException_whenPasswordIsWrong() {

        String username = "parneel";
        String password = "password123";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(Role.USER);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(password, user.getPassword()))
                .thenReturn(false);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loginUser(username, password)
        );

        assertEquals(
                "Invalid username or password",
                exception.getMessage()
        );

        verify(jwtUtil, never())
                .generateToken(username, user.getRole().toString());
    }
}
