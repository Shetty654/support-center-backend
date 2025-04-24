package com.example.support_center_backend.controllers;

import com.example.support_center_backend.payload.OtpRequest;
import com.example.support_center_backend.payload.VerifyOtpRequest;
import com.example.support_center_backend.services.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    private SmsService smsService;
    @PostMapping("/sendotp")
    public ResponseEntity<String> sendotp(@RequestBody OtpRequest request) {
        String phone = request.getPhone();
        if(phone == null || phone.length() < 13 || !phone.startsWith("+91") || phone.substring(3).length()!=10){
            return new ResponseEntity<>("Enter valid number", HttpStatus.BAD_REQUEST);
        }
        String response = smsService.sendSms(phone);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/verifyotp")
    public ResponseEntity<String> verifyotp(@RequestBody VerifyOtpRequest request) {
        if (request.getCode() == null || request.getCode().length() != 6) {
            return new ResponseEntity<>("Enter valid code", HttpStatus.BAD_REQUEST);
        }
        boolean response = smsService.verifySms(request.getPhone(), request.getCode());
        if (response) {
            return new ResponseEntity<>("OTP verified successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);
    }

}
