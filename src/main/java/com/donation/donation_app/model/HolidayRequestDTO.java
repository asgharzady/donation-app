package com.donation.donation_app.model;

import lombok.Data;

import java.time.Instant;

@Data
public class HolidayRequestDTO {
    private String name;
    private Instant startTime;
    private Instant endTime;
}

