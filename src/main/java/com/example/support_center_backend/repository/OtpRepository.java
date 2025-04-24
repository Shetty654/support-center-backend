package com.example.support_center_backend.repository;

import com.example.support_center_backend.models.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    public Otp findByPhone(String phone);
}
