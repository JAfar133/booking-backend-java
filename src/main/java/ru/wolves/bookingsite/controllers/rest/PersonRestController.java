package ru.wolves.bookingsite.controllers.rest;

import jakarta.servlet.http.HttpSession;
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
import ru.wolves.bookingsite.models.Booking;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.models.dto.BookingDTO;
import ru.wolves.bookingsite.models.dto.PersonDTO;
import ru.wolves.bookingsite.security.PersonDetails;
import ru.wolves.bookingsite.services.impl.PersonServiceImpl;
import ru.wolves.bookingsite.util.PersonValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/person")
public class PersonRestController {
    private final PersonServiceImpl personService;
    private final ModelMapper modelMapper;

    private final PersonValidator personValidator;
    @Autowired
    public PersonRestController(PersonServiceImpl personService, ModelMapper modelMapper, PersonValidator personValidator) {
        this.personService = personService;
        this.modelMapper = modelMapper;
        this.personValidator = personValidator;
    }
    @GetMapping("/get-bookings/{id}")
    public ResponseEntity<?> getAllBookingsByPersonId(@PathVariable Long id) throws PersonNotFoundException {
        Person person = personService.findPerson(id);
        if(person.getBookingList()==null || person.getBookingList().isEmpty()){
            return ResponseEntity.badRequest().body("У пользователя нет броней");
        }
        List<BookingDTO> bookingDTOs = new ArrayList<>();
        for(Booking booking: person.getBookingList()){
            bookingDTOs.add(convertToBookingDTO(booking));
        }
        return ResponseEntity.ok(bookingDTOs);
    }
    @PostMapping
    public ResponseEntity<?> updatePerson(@RequestBody PersonDTO personDTO) throws PersonNotFoundException, NotValidPhoneNumberException, FieldIsEmptyException {
        Person person = convertToPerson(personDTO);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person1 = personDetails.getPerson();
        personValidator.validate(person);
        personService.updatePerson(person1.getId(), person);
        return ResponseEntity.ok(convertToPersonDTO(person));
    }
    @PostMapping("/update-email")
    public ResponseEntity<?> updateEmail(@RequestBody PersonDTO personDTO) throws PersonNotFoundException, PersonAlreadyExistException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person1 = personDetails.getPerson();
        person1 = personService.updatePersonEmail(person1.getId(),convertToPerson(personDTO));
        return ResponseEntity.ok(convertToPersonDTO(person1));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable Long id) throws PersonNotFoundException {
        personService.deletePerson(id);

        return ResponseEntity.ok("Person with id = "+id+"was deleted successfully");
    }

    @GetMapping("/showInfo")
    public ResponseEntity<PersonDTO> showPersonInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        return ResponseEntity.ok(convertToPersonDTO(personDetails.getPerson()));
    }

    @ExceptionHandler({
            PersonNotFoundException.class, FieldIsEmptyException.class,
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
    private BookingDTO convertToBookingDTO(Booking booking){
        return modelMapper.map(booking,BookingDTO.class);
    }



}
