package com.milkmanagement.service;

import com.milkmanagement.dto.CustomerDTO;
import com.milkmanagement.dto.CustomerSignupDTO;
import com.milkmanagement.dto.JwtResponse;
import com.milkmanagement.dto.LoginRequest;
import com.milkmanagement.entity.Customer;
import com.milkmanagement.entity.Role;
import com.milkmanagement.entity.User;
import com.milkmanagement.repository.CustomerRepository;
import com.milkmanagement.repository.RoleRepository;
import com.milkmanagement.repository.UserRepository;
import com.milkmanagement.util.JwtUtil;
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
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Transactional
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String jwt = jwtUtil.generateToken(
            org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                    .map(Role::getName)
                    .map(auth -> (GrantedAuthority) () -> auth)
                    .collect(Collectors.toList()))
                .build()
        );
        
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        
        return new JwtResponse(jwt, "Bearer", user.getId(), user.getUsername(), user.getEmail(), roles);
    }
    
    @Transactional
    public User registerUser(String username, String email, String password, Set<String> roleNames) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken!");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use!");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        
        Set<Role> roles = new HashSet<>();
        if (roleNames == null || roleNames.isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_CUSTOMER")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            roles.add(userRole);
        } else {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        }
        user.setRoles(roles);
        
        return userRepository.save(user);
    }
    
    @Transactional
    public CustomerDTO registerCustomer(CustomerSignupDTO signupDTO) {
        // Check if username already exists
        if (userRepository.existsByUsername(signupDTO.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(signupDTO.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        // Check if customer email already exists
        if (customerRepository.existsByEmail(signupDTO.getEmail())) {
            throw new RuntimeException("Email is already registered as a customer!");
        }
        
        // Create User account
        User user = new User();
        user.setUsername(signupDTO.getUsername());
        user.setEmail(signupDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        user.setEnabled(true);
        
        // Assign ROLE_CUSTOMER
        Set<Role> roles = new HashSet<>();
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("ROLE_CUSTOMER not found. Please ensure roles are initialized in the database."));
        roles.add(customerRole);
        user.setRoles(roles);
        
        User savedUser = userRepository.save(user);
        
        // Create Customer record with default values
        Customer customer = new Customer();
        customer.setName(signupDTO.getUsername()); // Use username as default name
        customer.setEmail(signupDTO.getEmail());
        customer.setMobileNumber("0000000000"); // Default mobile number, can be updated later
        customer.setDailyMilkQuantity(java.math.BigDecimal.ZERO);
        customer.setMilkType(Customer.MilkType.COW);
        customer.setDeliveryStatus(Customer.DeliveryStatus.ACTIVE);
        customer.setCreatedBy(savedUser);
        
        Customer savedCustomer = customerRepository.save(customer);
        
        // Convert to DTO
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(savedCustomer.getId());
        customerDTO.setName(savedCustomer.getName());
        customerDTO.setAddress(savedCustomer.getAddress());
        customerDTO.setMobileNumber(savedCustomer.getMobileNumber());
        customerDTO.setEmail(savedCustomer.getEmail());
        customerDTO.setDailyMilkQuantity(savedCustomer.getDailyMilkQuantity());
        customerDTO.setMilkType(savedCustomer.getMilkType());
        customerDTO.setDeliveryStatus(savedCustomer.getDeliveryStatus());
        customerDTO.setCreatedById(savedUser.getId());
        
        return customerDTO;
    }
}
