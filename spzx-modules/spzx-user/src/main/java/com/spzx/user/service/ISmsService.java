package com.spzx.user.service;

public interface ISmsService {
    Boolean sendPhoneCode(String phone, String code);
}
