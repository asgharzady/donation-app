package com.donation.donation_app.model;

import lombok.Data;

@Data
public class ResetPasswordReqDTO {
    private String phoneNo;
    private String currentPassword;
    private String newPassword;
}
