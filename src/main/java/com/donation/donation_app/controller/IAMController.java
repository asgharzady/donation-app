package com.donation.donation_app.controller;


import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.entity.IAM;
import com.donation.donation_app.model.IAMResponseDTO;
import com.donation.donation_app.model.LoginReqDTO;
import com.donation.donation_app.model.ProfileSetupReqDTO;
import com.donation.donation_app.model.ResponseDTO;
import com.donation.donation_app.model.SignupReqDTO;
import com.donation.donation_app.service.IAMService;
import com.donation.donation_app.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ResponseDTO> loginUser(@RequestBody LoginReqDTO request) {
        log.info("login request: " + request.getEmail());
        iamService.login(request);
        String token = jwtUtil.generateToken(request.getEmail());
        return ResponseEntity.ok().body(new ResponseDTO("Bearer " + token));
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

}
