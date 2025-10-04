package com.donation.donation_app.repository;

import com.donation.donation_app.entity.DeviceBinding;
import com.donation.donation_app.entity.IAM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceBindingRepository extends JpaRepository<DeviceBinding, Long> {
    Optional<DeviceBinding> findByUserNameAndDeviceId(String userName, String deviceId);

    List<DeviceBinding> findAllByUserName(String userName);

    boolean existsByUserName(String userName);
}
