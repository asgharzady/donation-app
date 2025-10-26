package com.donation.donation_app.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileSetupReqDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Mobile number is required")
    private String mobileNo;
    
    @NotBlank(message = "Timezone is required")
    private String timezone;
    
    @NotBlank(message = "Default payment method is required")
    private String defaultPaymentMethod;
}
