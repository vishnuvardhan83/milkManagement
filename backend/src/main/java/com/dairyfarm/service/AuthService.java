package com.dairyfarm.service;

import com.dairyfarm.dto.JwtResponse;
import com.dairyfarm.dto.LoginRequest;
import com.dairyfarm.dto.SignupRequest;
import com.dairyfarm.entity.Role;
import com.dairyfarm.entity.User;
import com.dairyfarm.repository.RoleRepository;
import com.dairyfarm.repository.UserRepository;
import com.dairyfarm.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Transactional
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String jwt = jwtUtil.generateTokenWithRoles(
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal(),
                authentication.getAuthorities());
        String refreshToken = jwtUtil.generateRefreshToken(
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal());
        
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        
        return new JwtResponse(jwt, "Bearer", refreshToken, user.getId(), user.getUsername(), user.getEmail(), roles);
    }
    
    @Transactional
    public User registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }
        
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setFullName(signUpRequest.getFullName());
        user.setPhone(signUpRequest.getPhone());
        user.setEnabled(true);
        
        Set<Role> roles = new HashSet<>();
        String roleName = signUpRequest.getRole() != null ? signUpRequest.getRole() : "ROLE_CUSTOMER";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(role);
        user.setRoles(roles);
        
        return userRepository.save(user);
    }
}

