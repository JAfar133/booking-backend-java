package ru.wolves.bookingsite.controllers.rest;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.wolves.bookingsite.exceptions.FieldIsEmptyException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.NotValidPhoneNumberException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonAlreadyExistException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonNotFoundException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.SmsCodeIsNotCorrectException;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.models.SmsCode;
import ru.wolves.bookingsite.models.dto.AuthenticationResponse;
import ru.wolves.bookingsite.models.dto.PersonDTO;
import ru.wolves.bookingsite.security.PersonDetails;
import ru.wolves.bookingsite.services.impl.PersonServiceImpl;
import ru.wolves.bookingsite.services.impl.SmsServiceImpl;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/sms")
public class SMSRestController {

    public final static String SESSION_KEY_SMS_CODE = "SESSION_KEY_SMS_CODE";
    private final PersonServiceImpl personService;
    private final SmsServiceImpl smsService;
    private final ModelMapper modelMapper;

    @Autowired
    public SMSRestController(PersonServiceImpl personService, SmsServiceImpl smsService, ModelMapper modelMapper) {
        this.personService = personService;
        this.smsService = smsService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/sendSms")
    public ResponseEntity<?> sendSms(@RequestParam("phoneNumber") String phoneNumber) {
        SmsCode smsCode = smsService.sendSms(phoneNumber);
        return ResponseEntity.ok().body(smsCode);
    }

    @PostMapping("/verifyCode-and-auth")
    public ResponseEntity<?> verifyCodeAndAuth(@RequestParam("phoneNumber") String phoneNumber,
                                                             @RequestParam String code) throws PersonNotFoundException, NotValidPhoneNumberException, SmsCodeIsNotCorrectException {

        boolean isValid = smsService.verifyCode(phoneNumber, code);
        if (isValid) {
            return ResponseEntity.ok(personService.authPersonByPhone(phoneNumber));
        } else {
            throw new SmsCodeIsNotCorrectException("Неверный код");
        }
    }
    @PostMapping("/verifyCode")
    public ResponseEntity<?> verifyCode(@RequestParam("phoneNumber") String phoneNumber,@RequestParam String code) throws PersonNotFoundException, PersonAlreadyExistException, NotValidPhoneNumberException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = personDetails.getPerson();
        if(person.getId().equals(personService.findPerson(person.getId()).getId())){
            boolean isValid = smsService.verifyCode(phoneNumber, code);
            if(isValid){
                person = personService.verifyPhoneNumber(person, phoneNumber);
            }
            else return ResponseEntity.badRequest().body("Неверный код");
        }
        else throw new PersonAlreadyExistException("Пользователь с таким номером телефона уже зарегистрирован");
    return ResponseEntity.ok(convertToPersonDTO(person));
    }

    @ExceptionHandler({
            PersonNotFoundException.class, SmsCodeIsNotCorrectException.class,
            NotValidPhoneNumberException.class, PersonAlreadyExistException.class
    })
    private ResponseEntity<?> handler(Exception e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }


    private Person convertToPerson(PersonDTO personDTO){
        return modelMapper.map(personDTO,Person.class);
    }
    private PersonDTO convertToPersonDTO(Person person){
        return modelMapper.map(person,PersonDTO.class);
    }

}
