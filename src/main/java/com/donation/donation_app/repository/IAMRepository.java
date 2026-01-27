package com.donation.donation_app.repository;

import com.donation.donation_app.entity.IAM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAMRepository extends JpaRepository<IAM, Long> {

    IAM findByPhoneNo(String phoneNo);

    boolean existsByPhoneNo(String phoneNo);


}
