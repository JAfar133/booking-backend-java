package ru.wolves.bookingsite.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.wolves.bookingsite.exceptions.FieldIsEmptyException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.NotValidPhoneNumberException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonNotFoundException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.SmsCodeIsNotCorrectException;
import ru.wolves.bookingsite.exceptions.PlaceIsNotFoundException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.BookingNotFoundException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.PlaceIsNotFreeException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.TimeEndIsBeforeOrEqualsTimeStartException;
import ru.wolves.bookingsite.models.Booking;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.models.dto.BookingDTO;
import ru.wolves.bookingsite.models.dto.PersonDTO;
import ru.wolves.bookingsite.services.impl.BookingServiceImpl;
import ru.wolves.bookingsite.services.impl.RoomHallServiceImpl;
import ru.wolves.bookingsite.util.BookingValidator;
import ru.wolves.bookingsite.util.PersonValidator;

import java.util.ArrayList;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://195.133.49.102:8081")
@RequestMapping("/booking")
public class BookingRestController {

    private final BookingServiceImpl bookingService;
    private final BookingValidator bookingValidator;
    private final PersonValidator personValidator;
    private final RoomHallServiceImpl roomHallService;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    @Autowired
    public BookingRestController(BookingServiceImpl bookingService, BookingValidator bookingValidator,
                                 PersonValidator personValidator, RoomHallServiceImpl roomHallService,
                                 ObjectMapper objectMapper, ModelMapper modelMapper) {
        this.bookingService = bookingService;
        this.bookingValidator = bookingValidator;
        this.personValidator = personValidator;
        this.roomHallService = roomHallService;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        objectMapper.registerModule(new JavaTimeModule());
    }

    @PostMapping("/valid-booking")
    public ResponseEntity<?> validBookingAndAddToSession(
            @RequestBody BookingDTO bookingDTO) throws PlaceIsNotFoundException, FieldIsEmptyException, TimeEndIsBeforeOrEqualsTimeStartException {
        Booking booking = convertToBooking(bookingDTO);

        bookingValidator.validate(booking);

        return ResponseEntity.ok("Booking is valid");
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveBookingWithPersonDetails(
            @RequestBody BookingDTO bookingDTO) throws FieldIsEmptyException, TimeEndIsBeforeOrEqualsTimeStartException, NotValidPhoneNumberException, PersonNotFoundException {

        Person person = convertToPerson(bookingDTO.getCustomer());
        Booking booking = convertToBooking(bookingDTO);

        bookingValidator.validate(booking);

        personValidator.validate(person);

        bookingService.savePersonWithBooking(booking);

        return ResponseEntity.ok().body(convertToBookingDTO(booking));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) throws BookingNotFoundException {
        Booking booking = bookingService.findBooking(id);
        return ResponseEntity.ok(convertToBookingDTO(booking));
    }
    @GetMapping
    public ResponseEntity<?> getAllBookings(){
        List<Booking> allBookings = bookingService.findAllBooking();
        List<BookingDTO> bookingsDTO = new ArrayList<>();
        for(Booking b: allBookings) {
            bookingsDTO.add(convertToBookingDTO(b));
        }
        return ResponseEntity.ok(bookingsDTO);
    }
    @GetMapping("/get-all-unconfirmed")
    public ResponseEntity<?> getAllUnconfirmedBookings(){
        List<Booking> allBooking = bookingService.findAllUnConfirmedBooking();
        if(allBooking==null) {
            ResponseEntity.badRequest();
        }
        return ResponseEntity.ok(allBooking);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO) throws BookingNotFoundException, FieldIsEmptyException, TimeEndIsBeforeOrEqualsTimeStartException {
        Booking booking = convertToBooking(bookingDTO);
        bookingService.updateBooking(id, booking);

        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) throws BookingNotFoundException {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Booking was deleted successfully");
    }

    @PostMapping("/delete-all")
    public ResponseEntity<?> deleteBookings(@RequestBody List<BookingDTO> bookingDTOS){
        bookingService.deleteBookings(bookingDTOS);
        return ResponseEntity.ok("bookings was deleted");
    }

    @ExceptionHandler({
            PlaceIsNotFoundException.class, FieldIsEmptyException.class,
            NotValidPhoneNumberException.class, TimeEndIsBeforeOrEqualsTimeStartException.class,
            BookingNotFoundException.class, PlaceIsNotFreeException.class,
            Exception.class, SmsCodeIsNotCorrectException.class
    })
    private ResponseEntity<?> handle(Exception e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    private Person convertToPerson(PersonDTO personDTO){
        return modelMapper.map(personDTO,Person.class);
    }
    private PersonDTO convertToPersonDTO(Person person){
        return modelMapper.map(person,PersonDTO.class);
    }
    private Booking convertToBooking(BookingDTO bookingDTO){
        return modelMapper.map(bookingDTO,Booking.class);
    }
    private BookingDTO convertToBookingDTO(Booking booking){
        return modelMapper.map(booking,BookingDTO.class);
    }
}
