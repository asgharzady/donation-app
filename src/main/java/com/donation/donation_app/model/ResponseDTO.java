package com.donation.donation_app.model;

import lombok.Data;

@Data
public class ResponseDTO {
    private String english;
    private String spanish;
    public ResponseDTO(String english,String spanish) {
        this.english = english;
        this.spanish = spanish;
    }

    public ResponseDTO(String english) {
        this.english = english;
        this.spanish = "";
    }
}
