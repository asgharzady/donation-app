package com.donation.donation_app.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupReqDTO {

    private String username;
    @NotBlank(message = "Password is mandatory")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{12,}$",
            message = "Password must be at least 12 characters long, include at least one uppercase letter, one number, and one special character.",
            groups = {PasswordRequired.class, PasswordOptional.class}
    )
    private String password;
    private String designation;
    private Boolean isBlocked;

    public interface PasswordRequired {}
    public interface PasswordOptional {}

}
