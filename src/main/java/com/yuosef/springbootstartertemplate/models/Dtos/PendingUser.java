package com.yuosef.springbootstartertemplate.models.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingUser {

    private UserAccountInfo user;

    private String otp;

}