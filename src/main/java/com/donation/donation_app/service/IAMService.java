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
        IAM checkExistingUser = iamRepository.findByEmail(request.getEmail());
        if (checkExistingUser != null) {
            throw new CustomException("username already taken !");
        } else {
            log.info("creating account with username " + request.getEmail());
            IAM iam = new IAM();
            iam.setFirstName(request.getFirstName());
            iam.setLastName(request.getLastName());
            iam.setEmail(request.getEmail());
            iam.setDob(request.getDob());
            iam.setEmail(request.getEmail());
            iam.setPassword(passwordEncoder.encode(request.getPassword()));
            iam.setBlocked(false);
            iamRepository.save(iam);
        }
    }

    public void login(LoginReqDTO request) {
        IAM checkExistingUser = iamRepository.findByEmail(request.getEmail());
        if (checkExistingUser == null) {
            log.info("username not found for user: " + request.getEmail());
            throw new CustomException("username not found !");
        } else if (!passwordEncoder.matches(request.getPassword(), checkExistingUser.getPassword())) {
            log.info("wrong password for user: " + request.getEmail());
            throw new CustomException("wrong password !");
        }
    }

    @Transactional
    public void profileSetup(ProfileSetupReqDTO request) {
        IAM existingUser = iamRepository.findByEmail(request.getEmail());
        if (existingUser == null) {
            log.info("User not found for email: " + request.getEmail());
            throw new CustomException("User account not found with this email!");
        } else {
            log.info("Updating profile for user: " + request.getEmail());
            existingUser.setMobileNo(request.getMobileNo());
            existingUser.setTimezone(request.getTimezone());
            existingUser.setDefaultPaymentMethod(request.getDefaultPaymentMethod());
            iamRepository.save(existingUser);
            log.info("Profile updated successfully for user: " + request.getEmail());
        }
    }

    public IAMResponseDTO getByEmail(String email) {
        IAM user = iamRepository.findByEmail(email);
        if (user == null) {
            log.info("User not found for email: " + email);
            throw new CustomException("User not found with this email!");
        }
        log.info("Retrieved user successfully for email: " + email);
        
        // Convert IAM entity to IAMResponseDTO using static toDTO method
        return IAMResponseDTO.toDTO(user);
    }

    /**
     * Saves a refresh token for a user
     * @param token the refresh token string
     * @param email the user's email
     * @param expiryDate the expiration date of the token
     */
    @Transactional
    public void saveRefreshToken(String token, String email, Instant expiryDate) {
        IAM user = iamRepository.findByEmail(email);
        if (user == null) {
            throw new CustomException("User not found with email: " + email);
        }

        // Delete any existing refresh tokens for this user
        refreshTokenRepository.deleteByUserEmail(email);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expiryDate);
        refreshTokenRepository.save(refreshToken);
        log.info("Refresh token saved for user: " + email);
    }

    /**
     * Validates a refresh token and returns the associated user
     * @param refreshToken the refresh token to validate
     * @return the IAM user associated with the token
     */
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
            log.info("Refresh token expired for user: " + tokenEntity.getUser().getEmail());
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new CustomException("Refresh token expired");
        }

        // Verify the user from token matches the database user
        String tokenEmail = jwtUtil.extractUsernameFromRefreshToken(refreshToken);
        if (!tokenEmail.equals(tokenEntity.getUser().getEmail())) {
            log.info("Token email mismatch");
            throw new CustomException("Invalid refresh token");
        }

        log.info("Refresh token validated for user: " + tokenEntity.getUser().getEmail());
        return tokenEntity.getUser();
    }

    /**
     * Deletes a refresh token
     * @param token the refresh token to delete
     */
    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
        log.info("Refresh token deleted");
    }
//
//    public void updateUser(SignupReqDTO request) {
//        IAM checkExistingUser = iamRepository.findByUserName(request.getUsername());
//        if (checkExistingUser == null) {
//            throw new CustomException("username not found !");
//        } else {
//            if (request.getDesignation() != null) {
//                checkExistingUser.setDesignation(request.getDesignation());
//            }
//            if (request.getIsBlocked() != null) {
//                checkExistingUser.setBlocked(request.getIsBlocked());
//            }
//            if (request.getPassword() != null) {
//                String encodedNewPassword = passwordEncoder.encode(request.getPassword());
//                // Parse the stored string into a list of previous passwords
//                List<String> previousPasswords = new ArrayList<>();
//                if (checkExistingUser.getPreviousPasswords() != null && !checkExistingUser.getPreviousPasswords().isEmpty()) {
//                    previousPasswords = new ArrayList<>(Arrays.asList(checkExistingUser.getPreviousPasswords().split(",")));
//                }
//
//                // Check if the new password matches any of the previous passwords
//                if (previousPasswords.stream()
//                        .anyMatch(previousPassword -> passwordEncoder.matches(request.getPassword(), previousPassword))) {
//                    log.info("The new password cannot be one of the previous 3 passwords.");
//                    throw new CustomException("The new password cannot be one of the previous 3 passwords.");
//                }
//
//                // Update the password
//                checkExistingUser.setPassword(encodedNewPassword);
//
//                // Update the previous passwords list
//                if (previousPasswords.size() >= 4) {
//                    previousPasswords.remove(0);
//                }
//                previousPasswords.add(encodedNewPassword);
//
//                // Convert the updated list back to a comma-separated string
//                String updatedPreviousPasswords = String.join(",", previousPasswords);
//                checkExistingUser.setPreviousPasswords(updatedPreviousPasswords);
//
//            }
//            iamRepository.save(checkExistingUser);
//        }
//    }
//
//    public PaginatedUsers getAllPaginsated(Pageable pageable) {
//        PaginatedUsers response = new PaginatedUsers();
//        response.setData(iamRepository.findAll(pageable).stream().toList());
//        response.setTotalDocuments(iamRepository.count());
//        return response;
//    }
//
//    public IAM getByUsername(String username) {
//        IAM iam = iamRepository.findByUserName(username);
//
//        if (iam == null) {
//            throw new CustomException("username not found");
//        }
//
//        return iam;
//    }
//
//    @Transactional
//    public Boolean deleteUser(String username) {
//        if (iamRepository.existsByUserName(username)) {
//            iamRepository.deleteByUserName(username);
//            return true;
//        }
//        throw new CustomException("username not found");
//    }

}
