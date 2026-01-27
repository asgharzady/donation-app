package com.donation.donation_app.repository;

import com.donation.donation_app.entity.Card;
import com.donation.donation_app.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> getAllByPhoneNo(String phoneNo);

}




