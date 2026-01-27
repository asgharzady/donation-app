package com.donation.donation_app.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileSetupReqDTO {
    @NotBlank(message = "Phone number is required")
    private String phoneNo;
    
    @NotBlank(message = "Timezone is required")
    private String timezone;
    
    @NotBlank(message = "Default payment method is required")
    private String defaultPaymentMethod;
}
