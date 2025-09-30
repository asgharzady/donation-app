package com.donation.donation_app.model;

import lombok.Data;

@Data
public class ResponseDTO {
    private String message;
    public ResponseDTO(String message) {
        this.message = message;
    }
}
