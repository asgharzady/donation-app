package com.donation.donation_app.controller;


import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.entity.IAM;
import com.donation.donation_app.model.*;
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
        log.info("Sign up request for phoneNo: " + request.getPhoneNo());
        iamService.SignUp(request);
        log.info("returning ok for signup req: " + request.getPhoneNo());
        return ResponseEntity.ok(new ResponseDTO("sign up successful"));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<TokenResponseDTO> loginUser(@RequestBody LoginReqDTO request) {
        log.info("login request: " + request.getPhoneNo());
        iamService.login(request);
        
        // Generate access token
        String accessToken = jwtUtil.generateToken(request.getPhoneNo());
        
        // Generate refresh token
        String refreshToken = jwtUtil.generateRefreshToken(request.getPhoneNo());
        
        // Calculate expiry date (7 days from now)
        Instant expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60);
        
        // Save refresh token to database
        iamService.saveRefreshToken(refreshToken, request.getPhoneNo(), expiryDate);
        
        log.info("Login successful for user: " + request.getPhoneNo());
        return ResponseEntity.ok().body(new TokenResponseDTO(accessToken, refreshToken));
    }

    @PostMapping(value = "/login-profile")
    public ResponseEntity<TokenResponseProfileDTO> loginUserProfile(@RequestBody LoginReqDTO request) {
        log.info("login request: " + request.getPhoneNo());
        iamService.login(request);

        // Generate access token
        String accessToken = jwtUtil.generateToken(request.getPhoneNo());

        // Generate refresh token
        String refreshToken = jwtUtil.generateRefreshToken(request.getPhoneNo());

        // Calculate expiry date (7 days from now)
        Instant expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60);

        // Save refresh token to database
        iamService.saveRefreshToken(refreshToken, request.getPhoneNo(), expiryDate);

        log.info("Login successful for user: " + request.getPhoneNo());

        IAMResponseDTO user = iamService.getByPhoneNo(request.getPhoneNo());
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(accessToken, refreshToken);
        return ResponseEntity.ok().body(new TokenResponseProfileDTO(tokenResponseDTO, user));
    }





    @PostMapping(value = "/profile-setup")
    public ResponseEntity<ResponseDTO> profileSetup(@Validated @RequestBody ProfileSetupReqDTO request) {
        log.info("Profile setup request for phoneNo: " + request.getPhoneNo());
        String tokenPhoneNo = JwtUtil.getAuthenticatedPhoneNo();
        if (tokenPhoneNo == null || !tokenPhoneNo.equals(request.getPhoneNo())) {
            throw new CustomException("Unauthorized: wrong token");
        }
        iamService.profileSetup(request);
        log.info("Profile setup completed successfully for phoneNo: " + request.getPhoneNo());
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
        String newAccessToken = jwtUtil.generateToken(user.getPhoneNo());
        
        log.info("New access token generated for user: " + user.getPhoneNo());
        return ResponseEntity.ok().body(new TokenResponseDTO(newAccessToken, request.getRefreshToken()));
    }

    @GetMapping(value = "/{phoneNo}")
    public ResponseEntity<IAMResponseDTO> getUserByPhoneNo(@PathVariable("phoneNo") String phoneNo) {
        log.info("Get user request for phoneNo: " + phoneNo);
        String tokenPhoneNo = JwtUtil.getAuthenticatedPhoneNo();
        if (tokenPhoneNo == null || !tokenPhoneNo.equals(phoneNo)) {
            throw new CustomException("Unauthorized: wrong token");
        }
        
        IAMResponseDTO user = iamService.getByPhoneNo(phoneNo);
        log.info("Returning user data for phoneNo: " + phoneNo);
        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "is-profile-completed/{phoneNo}")
    public ResponseEntity<Boolean> isProfileCompleted(@PathVariable("phoneNo") String phoneNo) {
        log.info("get is profile completed for phoneNo: " + phoneNo);
        String tokenPhoneNo = JwtUtil.getAuthenticatedPhoneNo();
        if (tokenPhoneNo == null || !tokenPhoneNo.equals(phoneNo)) {
            throw new CustomException("Unauthorized: wrong token");
        }
        boolean isProfileCompleted = iamService.isProfileCompleted(phoneNo);
        log.info("Returning is profile completed for phoneNo: " + phoneNo);
        return ResponseEntity.ok(isProfileCompleted);
    }



}
