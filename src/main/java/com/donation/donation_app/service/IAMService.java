package com.donation.donation_app.service;

import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.entity.IAM;
import com.donation.donation_app.entity.RefreshToken;
import com.donation.donation_app.model.IAMResponseDTO;
import com.donation.donation_app.model.LoginReqDTO;
import com.donation.donation_app.model.ProfileSetupReqDTO;
import com.donation.donation_app.model.SignupReqDTO;
import com.donation.donation_app.model.ResetPasswordReqDTO;
import com.donation.donation_app.model.ForgetPasswordReqDTO;
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
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new CustomException("email can not be empty !", "¡El correo electrónico no puede estar vacío!");
        }
        if (checkExistingUser != null) {
            throw new CustomException("username already taken !", "¡El nombre de usuario ya está en uso!");
        } else {
            log.info("creating account with username " + request.getPhoneNo());
            IAM iam = new IAM();
            iam.setFirstName(request.getFirstName());
            iam.setLastName(request.getLastName());
            iam.setPhoneNo(request.getPhoneNo());
            iam.setDob(request.getDob());
            iam.setEmail(request.getEmail());
            iam.setPassword(passwordEncoder.encode(request.getPassword()));
            iam.setBlocked(false);
            iamRepository.save(iam);
        }
    }

    public void login(LoginReqDTO request) {
        IAM checkExistingUser = iamRepository.findByPhoneNo(request.getPhoneNo());
        if (checkExistingUser == null) {
            log.info("username not found for user: " + request.getPhoneNo());
            throw new CustomException("username not found !", "¡Nombre de usuario no encontrado!");
        } else if (!passwordEncoder.matches(request.getPassword(), checkExistingUser.getPassword())) {
            log.info("wrong password for user: " + request.getPhoneNo());
            throw new CustomException("wrong password !", "¡Contraseña incorrecta!");
        }
    }

    @Transactional
    public void profileSetup(ProfileSetupReqDTO request) {
        IAM existingUser = iamRepository.findByPhoneNo(request.getPhoneNo());
        if (existingUser == null) {
            log.info("User not found for phoneNo: " + request.getPhoneNo());
            throw new CustomException("User account not found with this phone number!", "¡Usuario no encontrado con este número de teléfono!");
        } else {
            log.info("Updating profile for user: " + request.getPhoneNo());
            existingUser.setTimezone(request.getTimezone());
            existingUser.setDefaultPaymentMethod(request.getDefaultPaymentMethod());
            iamRepository.save(existingUser);
            log.info("Profile updated successfully for user: " + request.getPhoneNo());
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordReqDTO request) {
        IAM user = iamRepository.findByPhoneNo(request.getPhoneNo());
        if (user == null) {
            log.info("User not found for phoneNo: " + request.getPhoneNo());
            throw new CustomException("User not found!", "¡Usuario no encontrado!");
        } else if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.info("Wrong current password for user: " + request.getPhoneNo());
            throw new CustomException("wrong password !", "¡Contraseña incorrecta!");
        } else {
            log.info("Resetting password for user: " + request.getPhoneNo());
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            iamRepository.save(user);
        }
    }

    @Transactional
    public void forgetPassword(ForgetPasswordReqDTO request) {
        IAM user = iamRepository.findByPhoneNo(request.getPhoneNo());
        if (user == null) {
            log.info("User not found for phoneNo: " + request.getPhoneNo());
            throw new CustomException("User not found!", "¡Usuario no encontrado!");
        } else if (request.getEmail() == null || !request.getEmail().equals(user.getEmail())) {
            log.info("Email mismatch for user: " + request.getPhoneNo());
            throw new CustomException("Email does not match our records!", "¡El correo electrónico no coincide con nuestros registros!");
        } else {
            log.info("Resetting forgotten password for user: " + request.getPhoneNo());
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            iamRepository.save(user);
        }
    }

    public IAMResponseDTO getByPhoneNo(String phoneNo) {
        IAM user = iamRepository.findByPhoneNo(phoneNo);
        if (user == null) {
            log.info("User not found for phoneNo: " + phoneNo);
            throw new CustomException("User not found with this phone number!", "¡Usuario no encontrado con este número de teléfono!");
        }
        log.info("Retrieved user successfully for phoneNo: " + phoneNo);

        // Convert IAM entity to IAMResponseDTO using static toDTO method
        return IAMResponseDTO.toDTO(user);
    }

    /**
     * Saves a refresh token for a user
     * 
     * @param token      the refresh token string
     * @param phoneNo    the user's phone number
     * @param expiryDate the expiration date of the token
     */
    @Transactional
    public void saveRefreshToken(String token, String phoneNo, Instant expiryDate) {
        IAM user = iamRepository.findByPhoneNo(phoneNo);
        if (user == null) {
            throw new CustomException("User not found with phoneNo: " + phoneNo, "Usuario no encontrado con el número: " + phoneNo);
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
            throw new CustomException("Invalid refresh token", "Token de actualización inválido");
        }

        // Check if token exists in database
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    log.info("Refresh token not found in database");
                    return new CustomException("Invalid refresh token", "Token de actualización inválido");
                });

        // Check if token is expired
        if (tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            log.info("Refresh token expired for user: " + tokenEntity.getUser().getPhoneNo());
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new CustomException("Refresh token expired", "El token de actualización ha expirado");
        }

        // Verify the user from token matches the database user
        String tokenPhoneNo = jwtUtil.extractUsernameFromRefreshToken(refreshToken);
        if (!tokenPhoneNo.equals(tokenEntity.getUser().getPhoneNo())) {
            log.info("Token phoneNo mismatch");
            throw new CustomException("Invalid refresh token", "Token de actualización inválido");
        }

        log.info("Refresh token validated for user: " + tokenEntity.getUser().getPhoneNo());
        return tokenEntity.getUser();
    }

    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
        log.info("Refresh token deleted");
    }

    public boolean isProfileCompleted(String phoneNo) {
        IAM user = iamRepository.findByPhoneNo(phoneNo);
        if (user == null) {
            throw new CustomException("User not found with phoneNo: " + phoneNo, "Usuario no encontrado con el número: " + phoneNo);
        }
        return user.isComplete();

    }
}
