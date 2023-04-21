package ru.wolves.bookingsite.services.impl;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.wolves.bookingsite.models.SmsCode;
import ru.wolves.bookingsite.services.SmsService;

@Service
public class SmsServiceImpl implements SmsService {

    private static org.apache.log4j.Logger log = Logger.getLogger(SmsServiceImpl.class);

    @Value("${TWILIO_ACCOUNT_SID}")
    String ACCOUNT_SID;

    @Value("${TWILIO_AUTH_TOKEN}")
    String AUTH_TOKEN;

    @Value("${TWILIO_OUTGOING_SMS_NUMBER}")
    String OUTGOING_SMS_NUMBER;

    private SmsCode smsCode;

    @PostConstruct
    private void setup() {
//        Twilio.init(ACCOUNT_SID,AUTH_TOKEN);
    }

    @Override
    public SmsCode sendSms(String phoneNumber) {
        String code = RandomStringUtils.randomNumeric(6);
        smsCode = new SmsCode(code,60);
//        Message message = Message.creator(
//                new PhoneNumber(phoneNumber),
//                new PhoneNumber(OUTGOING_SMS_NUMBER),
//                "Код подтверждения:" + smsCode.getCode()
//        ).create();
//        log.info("message: "+message.getStatus().toString());
        log.info("smsCode: "+smsCode);
        return smsCode;
    }

    @Override
    public boolean verifyCode(String phoneNumber, String code) {
        if(!this.smsCode.getCode().equals(code)) log.info("code is incorrect");
        return this.smsCode.getCode().equals(code);
    }


}
