package ru.wolves.bookingsite.controllers.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.wolves.bookingsite.exceptions.FieldIsEmptyException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonNotFoundException;
import ru.wolves.bookingsite.exceptions.PlaceIsNotFoundException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.BookingNotFoundException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.PlaceIsNotFreeException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.TimeEndIsBeforeOrEqualsTimeStartException;
import ru.wolves.bookingsite.models.Booking;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.models.dto.BookingDTO;
import ru.wolves.bookingsite.models.dto.PersonDTO;
import ru.wolves.bookingsite.services.impl.BookingServiceImpl;
import ru.wolves.bookingsite.services.impl.PersonServiceImpl;
import ru.wolves.bookingsite.util.BookingValidator;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminRestController {

    private final BookingServiceImpl bookingService;
    private final PersonServiceImpl personService;
    private final ModelMapper modelMapper;
    private final BookingValidator bookingValidator;

    @Autowired
    public AdminRestController(BookingServiceImpl bookingService, PersonServiceImpl personService, ModelMapper modelMapper, BookingValidator bookingValidator) {
        this.bookingService = bookingService;
        this.personService = personService;
        this.modelMapper = modelMapper;
        this.bookingValidator = bookingValidator;
    }
    @PostMapping("/booking-confirm/{id}")
    public ResponseEntity<?> confirm(@PathVariable("id") Long id) throws BookingNotFoundException {
        Booking booking = bookingService.findBooking(id);
        booking.setConfirmed(true);
        booking.setRejected(false);
        bookingService.updateBooking(id, booking);
        return ResponseEntity.ok(convertToBookingDTO(booking));
    }
    @PostMapping("/booking-reject/{id}")
    public ResponseEntity<?> reject(@PathVariable("id") Long id) throws BookingNotFoundException {
        Booking booking = bookingService.findBooking(id);
        booking.setRejected(true);
        booking.setConfirmed(false);
        bookingService.updateBooking(id, booking);
        return ResponseEntity.ok(convertToBookingDTO(booking));
    }
    @PostMapping("/booking-cancel-rejection/{id}")
    public ResponseEntity<?> cancelRejection(@PathVariable("id") Long id) throws BookingNotFoundException {
        Booking booking = bookingService.findBooking(id);
        booking.setRejected(false);
        booking.setConfirmed(false);
        bookingService.updateBooking(id, booking);
        return ResponseEntity.ok(convertToBookingDTO(booking));
    }
    @PostMapping("/booking-update/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable("id") Long id, @RequestBody BookingDTO bookingDTO) throws BookingNotFoundException, FieldIsEmptyException, TimeEndIsBeforeOrEqualsTimeStartException {
        Booking booking = convertToBooking(bookingDTO);
        bookingValidator.validate(booking);
        bookingService.updateBooking(id, booking);

        return ResponseEntity.ok(convertToBookingDTO(booking));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) throws BookingNotFoundException {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Booking was deleted successfully");
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) throws PersonNotFoundException {
        Person person = personService.findPerson(id);
        PersonDTO personDTO = convertToPersonDTO(person);
        return ResponseEntity.ok(personDTO);
    }
    @GetMapping("/get-all-person")
    public ResponseEntity<?> getAll(){
        List<Person> persons = personService.findAll();
        List<PersonDTO> personDTOS = getPersonDTOs(persons);
        return ResponseEntity.ok(personDTOS);
    }

    @ExceptionHandler({
            PlaceIsNotFoundException.class, PlaceIsNotFreeException.class,
            FieldIsEmptyException.class, TimeEndIsBeforeOrEqualsTimeStartException.class,
            BookingNotFoundException.class, PlaceIsNotFreeException.class
    })
    private ResponseEntity<?> handle(Exception e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    private Booking convertToBooking(BookingDTO bookingDTO){
        return modelMapper.map(bookingDTO,Booking.class);
    }
    private BookingDTO convertToBookingDTO(Booking booking){
        return modelMapper.map(booking,BookingDTO.class);
    }
    private List<PersonDTO> getPersonDTOs(List<Person> personList){
        List<PersonDTO> personDTOS = new ArrayList<>();
        for (Person person: personList) {
            personDTOS.add(convertToPersonDTO(person));
        }
        return personDTOS;
    }
    private PersonDTO convertToPersonDTO(Person person){
        return modelMapper.map(person,PersonDTO.class);
    }



}
