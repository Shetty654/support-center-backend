package com.example.support_center_backend.services;

import org.springframework.stereotype.Service;

public interface SmsService {
    public String sendSms(String to);

    public boolean verifySms(String phone, String code);
}
