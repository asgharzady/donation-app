package com.donation.donation_app.model.payment;

import com.donation.donation_app.entity.Payment;
import com.donation.donation_app.model.card.CardDTO;
import jakarta.validation.Valid;
import lombok.Data;

import java.time.Instant;

@Data
    public class PaymentHistoryResponseDTO {
    private String amount;
    private String status;
    private String phoneNo;
    private String toAccount;

    private Instant createdAt;


    public static PaymentHistoryResponseDTO toDTO(Payment payment){
        PaymentHistoryResponseDTO response = new PaymentHistoryResponseDTO();
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setPhoneNo(payment.getPhoneNo());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }
}
