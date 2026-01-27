package com.donation.donation_app.repository;

import com.donation.donation_app.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    ArrayList<Card> findAllByPhoneNo(String phoneNo);
}





