package com.parneel.FinTrack.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String registerUser(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(
                request.getUsername(),
                request.getPassword()
        );

        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody RegisterRequest request) {
        return userService.loginUser(
                request.getUsername(),
                request.getPassword()
        );
    }
}