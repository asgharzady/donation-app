package com.donation.donation_app.model.payment;

import com.donation.donation_app.model.card.CardDTO;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class PaymentRequest {
    @Valid
    private CardDTO card;
    private String amount;
    private String status;
    private String phoneNo;
    private String toAccount;

}
