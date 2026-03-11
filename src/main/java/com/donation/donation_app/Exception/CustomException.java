package com.donation.donation_app.Exception;

public class CustomException extends RuntimeException {
    String spanish;
    public CustomException(String message,String spanish) {
        super(message);
        this.spanish = spanish;
    }
}