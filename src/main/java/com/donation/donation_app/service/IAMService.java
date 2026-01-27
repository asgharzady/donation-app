package com.donation.donation_app.service;

import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.entity.IAM;
import com.donation.donation_app.entity.RefreshToken;
import com.donation.donation_app.model.IAMResponseDTO;
import com.donation.donation_app.model.LoginReqDTO;
import com.donation.donation_app.model.ProfileSetupReqDTO;
import com.donation.donation_app.model.SignupReqDTO;
import com.donation.donation_app.repository.IAMRepository;
import com.donation.donation_app.repository.RefreshTokenRepository;
import com.donation.donation_app.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class IAMService {

    @Autowired
    private IAMRepository iamRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger log = LoggerFactory.getLogger(IAMService.class);

    public void SignUp(SignupReqDTO request) {
        IAM checkExistingUser = iamRepository.findByPhoneNo(request.getPhoneNo());
        if (checkExistingUser != null) {
            throw new CustomException("username already taken !");
        } else {
            log.info("creating account with username " + request.getPhoneNo());
            IAM iam = new IAM();
            iam.setFirstName(request.getFirstName());
            iam.setLastName(request.getLastName());
            iam.setPhoneNo(request.getPhoneNo());
            iam.setDob(request.getDob());
            iam.setPassword(passwordEncoder.encode(request.getPassword()));
            iam.setBlocked(false);
            iamRepository.save(iam);
        }
    }

    public void login(LoginReqDTO request) {
        IAM checkExistingUser = iamRepository.findByPhoneNo(request.getPhoneNo());
        if (checkExistingUser == null) {
            log.info("username not found for user: " + request.getPhoneNo());
            throw new CustomException("username not found !");
        } else if (!passwordEncoder.matches(request.getPassword(), checkExistingUser.getPassword())) {
            log.info("wrong password for user: " + request.getPhoneNo());
            throw new CustomException("wrong password !");
        }
    }

    @Transactional
    public void profileSetup(ProfileSetupReqDTO request) {
        IAM existingUser = iamRepository.findByPhoneNo(request.getPhoneNo());
        if (existingUser == null) {
            log.info("User not found for phoneNo: " + request.getPhoneNo());
            throw new CustomException("User account not found with this phone number!");
        } else {
            log.info("Updating profile for user: " + request.getPhoneNo());
            existingUser.setTimezone(request.getTimezone());
            existingUser.setDefaultPaymentMethod(request.getDefaultPaymentMethod());
            iamRepository.save(existingUser);
            log.info("Profile updated successfully for user: " + request.getPhoneNo());
        }
    }

    public IAMResponseDTO getByPhoneNo(String phoneNo) {
        IAM user = iamRepository.findByPhoneNo(phoneNo);
        if (user == null) {
            log.info("User not found for phoneNo: " + phoneNo);
            throw new CustomException("User not found with this phone number!");
        }
        log.info("Retrieved user successfully for phoneNo: " + phoneNo);
        
        // Convert IAM entity to IAMResponseDTO using static toDTO method
        return IAMResponseDTO.toDTO(user);
    }

    /**
     * Saves a refresh token for a user
     * @param token the refresh token string
     * @param phoneNo the user's phone number
     * @param expiryDate the expiration date of the token
     */
    @Transactional
    public void saveRefreshToken(String token, String phoneNo, Instant expiryDate) {
        IAM user = iamRepository.findByPhoneNo(phoneNo);
        if (user == null) {
            throw new CustomException("User not found with phoneNo: " + phoneNo);
        }

        // Delete any existing refresh tokens for this user
        refreshTokenRepository.deleteByUserPhoneNo(phoneNo);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expiryDate);
        refreshTokenRepository.save(refreshToken);
        log.info("Refresh token saved for user: " + phoneNo);
    }

    @Transactional
    public IAM validateAndGetUser(String refreshToken) {
        // First validate the JWT token structure
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            log.info("Invalid refresh token structure");
            throw new CustomException("Invalid refresh token");
        }

        // Check if token exists in database
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    log.info("Refresh token not found in database");
                    return new CustomException("Invalid refresh token");
                });

        // Check if token is expired
        if (tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            log.info("Refresh token expired for user: " + tokenEntity.getUser().getPhoneNo());
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new CustomException("Refresh token expired");
        }

        // Verify the user from token matches the database user
        String tokenPhoneNo = jwtUtil.extractUsernameFromRefreshToken(refreshToken);
        if (!tokenPhoneNo.equals(tokenEntity.getUser().getPhoneNo())) {
            log.info("Token phoneNo mismatch");
            throw new CustomException("Invalid refresh token");
        }

        log.info("Refresh token validated for user: " + tokenEntity.getUser().getPhoneNo());
        return tokenEntity.getUser();
    }

    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
        log.info("Refresh token deleted");
    }

    public boolean isProfileCompleted(String phoneNo){
        IAM user = iamRepository.findByPhoneNo(phoneNo);
        if (user == null) {
            throw new CustomException("User not found with phoneNo: " + phoneNo);
        }
        return user.isComplete();

    }
}
