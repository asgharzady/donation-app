package com.donation.donation_app.model.payment;

import com.donation.donation_app.entity.Payment;
import lombok.Data;

import java.time.Instant;

@Data
public class PaymentStatisticsDTO {
    private Long totalDonation;
    private Long monthlyDonations;
    private Long pendingDonations = 0L;
    private Long completedDonations = 0L;


}

