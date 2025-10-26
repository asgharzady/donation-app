package com.donation.donation_app.model;

import com.donation.donation_app.entity.IAM;
import lombok.Data;

import java.time.Instant;

@Data
public class IAMResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dob;
    private String mobileNo;
    private String timezone;
    private String defaultPaymentMethod;
    private boolean isBlocked;
    private Instant createdAt;
    private Instant updatedAt;

    public static IAMResponseDTO toDTO(IAM iam) {
        if (iam == null) {
            return null;
        }
        
        IAMResponseDTO dto = new IAMResponseDTO();
        dto.setId(iam.getId());
        dto.setFirstName(iam.getFirstName());
        dto.setLastName(iam.getLastName());
        dto.setEmail(iam.getEmail());
        dto.setDob(iam.getDob());
        dto.setMobileNo(iam.getMobileNo());
        dto.setTimezone(iam.getTimezone());
        dto.setDefaultPaymentMethod(iam.getDefaultPaymentMethod());
        dto.setBlocked(iam.isBlocked());
        dto.setCreatedAt(iam.getCreatedAt());
        dto.setUpdatedAt(iam.getUpdatedAt());
        
        return dto;
    }
}
