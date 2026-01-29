package com.donation.donation_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseProfileDTO {
    private TokenResponseDTO tokenResponseDTO;
    private IAMResponseDTO iamResponseDTO;
}

