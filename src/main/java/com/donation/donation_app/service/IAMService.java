package com.donation.donation_app.service;

import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.entity.IAM;
import com.donation.donation_app.model.LoginReqDTO;
import com.donation.donation_app.model.SignupReqDTO;
import com.donation.donation_app.repository.IAMRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class IAMService {

    @Autowired
    private IAMRepository iamRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(IAMService.class);

    public void SignUp(SignupReqDTO request) {
        IAM checkExistingUser = iamRepository.findByUserName(request.getUsername());
        if (checkExistingUser != null) {
            throw new CustomException("username already taken !");
        } else {
            log.info("creating account with username " + request.getUsername());
            IAM iam = new IAM();
            iam.setUserName(request.getUsername());
            iam.setPassword(passwordEncoder.encode(request.getPassword()));
            iam.setPreviousPasswords(passwordEncoder.encode(request.getPassword()));
            iam.setDesignation(request.getDesignation());
            iam.setBlocked(false);
            iamRepository.save(iam);
        }
    }

    public void login(LoginReqDTO request) {
        IAM checkExistingUser = iamRepository.findByUserName(request.getUserName());
        if (checkExistingUser == null) {
            log.info("username not found for user: " + request.getUserName());
            throw new CustomException("username not found !");
        } else if (!passwordEncoder.matches(request.getPassword(), checkExistingUser.getPassword())) {
            log.info("wrong password for user: " + request.getUserName());
            throw new CustomException("wrong password !");
        }
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
