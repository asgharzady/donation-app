package com.donation.donation_app.entity;

import com.donation.donation_app.model.card.CardDTO;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Table(name = "card")
@Data
@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cardNo;
    private String name;
    private String expDate;
    private String cvv;
    private String phoneNo;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    public static CardDTO toDto(Card card){
        CardDTO response = new CardDTO();
        response.setCardNo(card.getCardNo());
        response.setName(card.getName());
        response.setCvv(card.getCvv());
        response.setExpDate(card.getExpDate());

        return response;
    }
}
