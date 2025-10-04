package com.donation.donation_app.service;

import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.entity.DeviceBinding;
import com.donation.donation_app.repository.DeviceBindingRepository;
import com.donation.donation_app.repository.IAMRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeviceBindingService {

    @Autowired
    private IAMRepository iamRepository;

    private final DeviceBindingRepository repository;

    public DeviceBindingService(DeviceBindingRepository repository) {
        this.repository = repository;
    }

    public DeviceBinding bindDevice(String userName, String deviceId) {

        if(!iamRepository.existsByUserName(userName))
            throw new CustomException("username not found");

        List<DeviceBinding> devices = repository.findAllByUserName(userName);
        for(DeviceBinding device:devices){
            device.setIsBinded(false);
        }
        repository.saveAll(devices);
        Optional<DeviceBinding> existing = repository.findByUserNameAndDeviceId(userName, deviceId);
        if (existing.isPresent()) {
            DeviceBinding deviceBinding = existing.get();
            deviceBinding.setIsBinded(true);
            return deviceBinding;
        }

        DeviceBinding deviceBinding = new DeviceBinding();
        deviceBinding.setUserName(userName);
        deviceBinding.setDeviceId(deviceId);
        deviceBinding.setIsBinded(true); // mark as bound

        return repository.save(deviceBinding);
    }

    public boolean verifyDevice(String userName, String deviceId) {
        return repository.findByUserNameAndDeviceId(userName, deviceId)
                .map(db -> db.getIsBinded())
                .orElse(false);
    }

    public List<DeviceBinding> listDevices(String userName) {
        return repository.findAllByUserName(userName);
    }

    public void unbindDevice(String userName, String deviceId) {
        DeviceBinding binding = repository.findByUserNameAndDeviceId(userName, deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found for this user."));
        binding.setIsBinded(false);
        repository.save(binding);
    }

}
