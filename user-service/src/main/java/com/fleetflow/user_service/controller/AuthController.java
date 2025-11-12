package com.fleetflow.user_service.controller;

import com.fleetflow.user_service.dto.AuthResponse;
import com.fleetflow.user_service.dto.ErrorResponse;
import com.fleetflow.user_service.dto.LoginRequest;
import com.fleetflow.user_service.dto.RegisterRequest;
import com.fleetflow.user_service.model.Role;
import com.fleetflow.user_service.model.User;
import com.fleetflow.user_service.repository.UserRepository;
import com.fleetflow.user_service.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: Username is already taken!"));
        }

        if (request.role() == null || request.role().isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: Role is required."));
        }

        Role userRole;
        try {
            userRole = Role.valueOf(request.role().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: Invalid role specified! Must be ROLE_MECHANIC, ROLE_LOGISTICIAN, or ROLE_ADMIN."));
        }

        Long repairShopId = null;
        Long warehouseId = null;

        switch (userRole) {
            case ROLE_MECHANIC:
                if (request.repairShopId() == null) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Error: repairShopId is required for MECHANIC role."));
                }
                if (request.warehouseId() != null) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Error: warehouseId must be null for MECHANIC role."));
                }
                repairShopId = request.repairShopId();
                break;

            case ROLE_LOGISTICIAN:
                if (request.warehouseId() == null) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Error: warehouseId is required for LOGISTICIAN role."));
                }
                if (request.repairShopId() != null) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Error: repairShopId must be null for LOGISTICIAN role."));
                }
                warehouseId = request.warehouseId();
                break;

            case ROLE_ADMIN:
                if (request.repairShopId() != null || request.warehouseId() != null) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Error: repairShopId and warehouseId must be null for ADMIN role."));
                }
                break;
        }

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                userRole,
                repairShopId,
                warehouseId
        );

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User userDetails = (User) authentication.getPrincipal();

        String jwtToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }
}
