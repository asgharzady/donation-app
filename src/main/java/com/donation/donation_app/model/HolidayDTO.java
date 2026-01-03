package com.donation.donation_app.model;

import lombok.Data;

import java.time.Instant;

@Data
public class HolidayDTO {
    private Long id;
    private String name;
    private Instant startTime;
    private Instant endTime;
    private Instant createdAt;
    private Instant updatedAt;
}

