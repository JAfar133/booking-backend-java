package ru.wolves.bookingsite.services;

import ru.wolves.bookingsite.models.SmsCode;

public interface SmsService {

    SmsCode sendSms(String phoneNumber);
    boolean verifyCode(String phoneNumber, String code);
}
