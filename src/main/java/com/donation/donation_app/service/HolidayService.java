package com.donation.donation_app.service;

import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.entity.Holiday;
import com.donation.donation_app.model.HolidayDTO;
import com.donation.donation_app.model.HolidayRequestDTO;
import com.donation.donation_app.repository.HolidayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private static final Logger log = LoggerFactory.getLogger(HolidayService.class);

    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public void createHoliday(HolidayRequestDTO request) {
        log.info("Creating holiday: " + request.getName());
        
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new CustomException("Holiday name is required");
        }
        if (request.getStartTime() == null) {
            throw new CustomException("Start time is required");
        }
        if (request.getEndTime() == null) {
            throw new CustomException("End time is required");
        }
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new CustomException("Start time must be before end time");
        }

        Holiday holiday = new Holiday();
        holiday.setName(request.getName());
        holiday.setStartTime(request.getStartTime());
        holiday.setEndTime(request.getEndTime());

        holidayRepository.save(holiday);
        log.info("Holiday created successfully: " + request.getName());
    }

    public void updateHoliday(Long id, HolidayRequestDTO request) {
        log.info("Updating holiday with id: " + id);
        
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("Holiday not found with id: " + id);
                    return new CustomException("Holiday not found with id: " + id);
                });

        // Only update fields that are passed (not null)
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            holiday.setName(request.getName());
        }
        if (request.getStartTime() != null) {
            holiday.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            holiday.setEndTime(request.getEndTime());
        }

        // Validate start time is before end time if both are set
        if (holiday.getStartTime() != null && holiday.getEndTime() != null) {
            if (holiday.getStartTime().isAfter(holiday.getEndTime())) {
                throw new CustomException("Start time must be before end time");
            }
        }

        holidayRepository.save(holiday);
        log.info("Holiday updated successfully with id: " + id);
    }

    public void deleteHoliday(Long id) {
        log.info("Deleting holiday with id: " + id);
        
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("Holiday not found with id: " + id);
                    return new CustomException("Holiday not found with id: " + id);
                });

        holidayRepository.delete(holiday);
        log.info("Holiday deleted successfully with id: " + id);
    }

    public List<HolidayDTO> getAllHolidays() {
        log.info("Retrieving all holidays");
        List<Holiday> holidays = holidayRepository.findAll();
        List<HolidayDTO> holidayDTOs = holidays.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        log.info("Retrieved " + holidayDTOs.size() + " holidays");
        return holidayDTOs;
    }

    private HolidayDTO convertToDTO(Holiday holiday) {
        HolidayDTO dto = new HolidayDTO();
        dto.setId(holiday.getId());
        dto.setName(holiday.getName());
        dto.setStartTime(holiday.getStartTime());
        dto.setEndTime(holiday.getEndTime());
        dto.setCreatedAt(holiday.getCreatedAt());
        dto.setUpdatedAt(holiday.getUpdatedAt());
        return dto;
    }
}

