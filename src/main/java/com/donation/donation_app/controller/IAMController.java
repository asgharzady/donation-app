package com.donation.donation_app.controller;


import com.donation.donation_app.model.LoginReqDTO;
import com.donation.donation_app.model.ResponseDTO;
import com.donation.donation_app.model.SignupReqDTO;
import com.donation.donation_app.service.IAMService;
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
        log.info("returning ok for login req: " + request.getEmail());
        return ResponseEntity.ok(new ResponseDTO("login successful"));
    }
//
//    @PutMapping
//    public ResponseEntity<Void> updateUser(@Validated(SignupReqDTO.PasswordOptional.class) @RequestBody SignupReqDTO request) {
//        log.info("updating user: " + request.getUsername());
//        iamService.updateUser(request);
//        log.info("returning ok for update user " + request.getUsername());
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/findAll/{page}/{size}")
//    public ResponseEntity<PaginatedUsers> getAll(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
//        return ResponseEntity.ok(iamService.getAllPaginsated(PageRequest.of(page, size)));
//    }
//
//    @GetMapping("/{username}")
//    public ResponseEntity<IAM> getByUsername(@PathVariable("username") String username) {
//        return ResponseEntity.ok(iamService.getByUsername(username));
//    }
//
//    @DeleteMapping("/{userName}")
//    public ResponseEntity<Void> deleteUser(@PathVariable("userName") String userName) {
//        iamService.deleteUser(userName);
//        return ResponseEntity.ok().build();
//    }


}
