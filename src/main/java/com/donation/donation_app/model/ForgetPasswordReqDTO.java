package com.donation.donation_app.model;

import lombok.Data;

@Data
public class ForgetPasswordReqDTO {
    private String phoneNo;
    private String email;
    private String newPassword;
}
