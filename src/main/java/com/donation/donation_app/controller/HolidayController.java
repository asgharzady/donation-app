package com.donation.donation_app.controller;

import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.model.HolidayDTO;
import com.donation.donation_app.model.HolidayRequestDTO;
import com.donation.donation_app.model.ResponseDTO;
import com.donation.donation_app.service.HolidayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("holiday")
public class HolidayController {

    private static final Logger log = LoggerFactory.getLogger(HolidayController.class);

    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<ResponseDTO> createHoliday(@Validated @RequestBody HolidayRequestDTO request) {
        log.info("Create holiday request: " + request.getName());
        holidayService.createHoliday(request);
        log.info("Holiday created successfully: " + request.getName());
        return ResponseEntity.ok(new ResponseDTO("Holiday created successfully"));
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<ResponseDTO> updateHoliday(@PathVariable("id") Long id, @RequestBody HolidayRequestDTO request) {
        log.info("Update holiday request for id: " + id);
        holidayService.updateHoliday(id, request);
        log.info("Holiday updated successfully for id: " + id);
        return ResponseEntity.ok(new ResponseDTO("Holiday updated successfully"));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteHoliday(@PathVariable("id") Long id) {
        log.info("Delete holiday request for id: " + id);
        holidayService.deleteHoliday(id);
        log.info("Holiday deleted successfully for id: " + id);
        return ResponseEntity.ok(new ResponseDTO("Holiday deleted successfully"));
    }

    @GetMapping(value = "/get-all")
    public ResponseEntity<List<HolidayDTO>> getAllHolidays() {
        log.info("Get all holidays request");
        List<HolidayDTO> holidays = holidayService.getAllHolidays();
        log.info("Returning " + holidays.size() + " holidays");
        return ResponseEntity.ok(holidays);
    }
}

