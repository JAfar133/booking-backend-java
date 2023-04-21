package ru.wolves.bookingsite.controllers.rest;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonNotFoundException;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.models.SmsCode;
import ru.wolves.bookingsite.models.dto.PersonDTO;
import ru.wolves.bookingsite.services.impl.PersonServiceImpl;
import ru.wolves.bookingsite.services.impl.SmsServiceImpl;

@RestController
@RequestMapping("/auth")
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
    public ResponseEntity<?> sendSms(@RequestBody String phoneNumber) {
        SmsCode smsCode = smsService.sendSms(phoneNumber);
        return ResponseEntity.ok().body(smsCode);
    }

    @PostMapping("/verifyCode")
    public ResponseEntity<?> verifyCode(@RequestBody PersonDTO person, @RequestParam String code) throws PersonNotFoundException {
        boolean isValid = smsService.verifyCode(person.getPhoneNumber(), code);
        if (isValid) {
            personService.savePerson(convertToPerson(person));
            Person p = personService.findPersonByPhone(person.getPhoneNumber());
            return ResponseEntity.ok().body(convertToPersonDTO(p));
        } else {
            return ResponseEntity.badRequest().body("code is incorrect");
        }
    }

    private Person convertToPerson(PersonDTO personDTO){
        return modelMapper.map(personDTO,Person.class);
    }
    private PersonDTO convertToPersonDTO(Person person){
        return modelMapper.map(person,PersonDTO.class);
    }

}
