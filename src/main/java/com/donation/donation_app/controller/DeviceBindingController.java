package com.donation.donation_app.controller;

import com.donation.donation_app.entity.DeviceBinding;
import com.donation.donation_app.service.DeviceBindingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-binding")
public class DeviceBindingController {

    private final DeviceBindingService service;

    public DeviceBindingController(DeviceBindingService service) {
        this.service = service;
    }

    @PostMapping("/bind/{userName}/{deviceId}")
    public DeviceBinding bindDevice(@PathVariable String userName,
                                    @PathVariable String deviceId) {
        return service.bindDevice(userName, deviceId);
    }
    @GetMapping("/verify/{userName}/{deviceId}")
    public boolean verifyDevice(@PathVariable String userName,
                                @PathVariable String deviceId) {
        return service.verifyDevice(userName, deviceId);
    }
    @GetMapping("/list/{userName}")
    public List<DeviceBinding> listDevices(@PathVariable String userName) {
        return service.listDevices(userName);
    }
    @PostMapping("/unbind/{userName}/{deviceId}")
    public String unbindDevice(@PathVariable String userName,
                               @PathVariable String deviceId) {
        service.unbindDevice(userName, deviceId);
        return "Device unbound successfully.";
    }
}







