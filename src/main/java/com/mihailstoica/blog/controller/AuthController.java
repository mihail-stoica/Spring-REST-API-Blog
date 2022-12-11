package com.mihailstoica.blog.controller;

import com.mihailstoica.blog.entity.Role;
import com.mihailstoica.blog.entity.User;
import com.mihailstoica.blog.exception.ResourceNotFoundException;
import com.mihailstoica.blog.payload.JWTAuthResponse;
import com.mihailstoica.blog.payload.LoginDto;
import com.mihailstoica.blog.payload.SignUpDto;
import com.mihailstoica.blog.repository.RoleRepository;
import com.mihailstoica.blog.repository.UserRepository;
import com.mihailstoica.blog.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
            RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // get token from tokenProvider
        String token = tokenProvider.generateToken(authentication);

        //return new ResponseEntity<>(new JWTAuthResponse(token), HttpStatus.OK);
        return ResponseEntity.ok(new JWTAuthResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto) {

        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(signUpDto.getName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        Role roles = roleRepository.findByName("ROLE_ADMIN").orElseThrow(
                () -> new ResourceNotFoundException("ROLE_ADMIN"));
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }
}
