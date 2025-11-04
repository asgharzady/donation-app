package com.donation.donation_app.model.payment;

import com.donation.donation_app.entity.Payment;
import com.donation.donation_app.model.card.CardDTO;
import jakarta.validation.Valid;
import lombok.Data;

@Data
    public class PaymentHistoryResponseDTO {
    private String amount;
    private String status;
    private String email;
    private String toAccount;


    public static PaymentHistoryResponseDTO toDTO(Payment payment){
        PaymentHistoryResponseDTO response = new PaymentHistoryResponseDTO();
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setEmail(payment.getEmail());
        response.setToAccount(payment.getToAccount());

        return response;
    }
}
