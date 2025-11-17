package com.donation.donation_app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.stream.Stream;

@Table(name = "iam")
@Data
@Entity
public class IAM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dob;
    private String mobileNo;
    private String timezone;
    private String defaultPaymentMethod;
    private String password;
    private boolean isBlocked = false;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;


    public boolean isComplete() {
        return Stream.of(firstName, lastName, email, dob, mobileNo,timezone,defaultPaymentMethod)
                .allMatch(f -> f != null && !f.trim().isEmpty());
    }
}
