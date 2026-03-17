package com.yuosef.springbootstartertemplate.models.Dtos;

public record VerifyOtpRequest
(
     String email,

     String otp
        )
    {}