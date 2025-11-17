package com.donation.donation_app.controller;


import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.entity.IAM;
import com.donation.donation_app.model.IAMResponseDTO;
import com.donation.donation_app.model.LoginReqDTO;
import com.donation.donation_app.model.ProfileSetupReqDTO;
import com.donation.donation_app.model.RefreshTokenRequestDTO;
import com.donation.donation_app.model.ResponseDTO;
import com.donation.donation_app.model.SignupReqDTO;
import com.donation.donation_app.model.TokenResponseDTO;
import com.donation.donation_app.service.IAMService;
import com.donation.donation_app.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("user")
public class IAMController {

    private static final Logger log = LoggerFactory.getLogger(IAMController.class);
    @Autowired
    private IAMService iamService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/sign-up")
    public ResponseEntity<ResponseDTO> SignUpUser(@Validated(SignupReqDTO.PasswordRequired.class) @RequestBody SignupReqDTO request) {
        log.info("Sign up request and designation: " + request.getEmail());
        iamService.SignUp(request);
        log.info("returning ok for signup req: " + request.getEmail());
        return ResponseEntity.ok(new ResponseDTO("sign up successful"));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<TokenResponseDTO> loginUser(@RequestBody LoginReqDTO request) {
        log.info("login request: " + request.getEmail());
        iamService.login(request);
        
        // Generate access token
        String accessToken = jwtUtil.generateToken(request.getEmail());
        
        // Generate refresh token
        String refreshToken = jwtUtil.generateRefreshToken(request.getEmail());
        
        // Calculate expiry date (7 days from now)
        Instant expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60);
        
        // Save refresh token to database
        iamService.saveRefreshToken(refreshToken, request.getEmail(), expiryDate);
        
        log.info("Login successful for user: " + request.getEmail());
        return ResponseEntity.ok().body(new TokenResponseDTO(accessToken, refreshToken));
    }

    @PostMapping(value = "/profile-setup")
    public ResponseEntity<ResponseDTO> profileSetup(@Validated @RequestBody ProfileSetupReqDTO request) {
        log.info("Profile setup request for email: " + request.getEmail());
        String tokenEmail = JwtUtil.getAuthenticatedEmail();
        if (tokenEmail == null || !tokenEmail.equals(request.getEmail())) {
            throw new CustomException("Unauthorized: wrong token");
        }
        iamService.profileSetup(request);
        log.info("Profile setup completed successfully for email: " + request.getEmail());
        return ResponseEntity.ok(new ResponseDTO("Profile setup successful"));
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<TokenResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        log.info("Refresh token request received");
        
        if (request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
            throw new CustomException("Refresh token is required");
        }
        
        // Validate refresh token and get user
        IAM user = iamService.validateAndGetUser(request.getRefreshToken());
        
        // Generate new access token for the user
        String newAccessToken = jwtUtil.generateToken(user.getEmail());
        
        log.info("New access token generated for user: " + user.getEmail());
        return ResponseEntity.ok().body(new TokenResponseDTO(newAccessToken, request.getRefreshToken()));
    }

    @GetMapping(value = "/{email}")
    public ResponseEntity<IAMResponseDTO> getUserByEmail(@PathVariable("email") String email) {
        log.info("Get user request for email: " + email);
        String tokenEmail = JwtUtil.getAuthenticatedEmail();
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            throw new CustomException("Unauthorized: wrong token");
        }
        
        IAMResponseDTO user = iamService.getByEmail(email);
        log.info("Returning user data for email: " + email);
        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "is-profile-completed/{email}")
    public ResponseEntity<Boolean> isProfileCompleted(@PathVariable("email") String email) {
        log.info("get is profile completed for email: " + email);
        String tokenEmail = JwtUtil.getAuthenticatedEmail();
        if (tokenEmail == null || !tokenEmail.equals(email)) {
            throw new CustomException("Unauthorized: wrong token");
        }
        boolean isProfileCompleted = iamService.isProfileCompleted(email);
        log.info("Returning is profile completed for email: " + email);
        return ResponseEntity.ok(isProfileCompleted);
    }



}
