package ru.wolves.bookingsite.controllers.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.wolves.bookingsite.exceptions.FieldIsEmptyException;
import ru.wolves.bookingsite.exceptions.PlaceIsNotFoundException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.BookingNotFoundException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.PlaceIsNotFreeException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.TimeEndIsBeforeOrEqualsTimeStartException;
import ru.wolves.bookingsite.models.Booking;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.models.dto.BookingDTO;
import ru.wolves.bookingsite.security.PersonDetails;
import ru.wolves.bookingsite.services.impl.BookingServiceImpl;
import ru.wolves.bookingsite.services.impl.RoomHallServiceImpl;
import ru.wolves.bookingsite.util.BookingValidator;

@RestController
@RequestMapping("/admin")
public class AdminRestController {

    private final BookingServiceImpl bookingService;

    @Autowired
    public AdminRestController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;

    }
    @PatchMapping("/booking/{id}")
    public ResponseEntity<?> confirm(@PathVariable("id") Long id) throws BookingNotFoundException {


        Booking booking = bookingService.findBooking(id);
        booking.setConfirmed(true);
        bookingService.updateBooking(id, booking);
        return ResponseEntity.ok(booking);
    }
    @GetMapping("/booking/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) throws BookingNotFoundException {
        Booking booking = bookingService.findBooking(id);
        return ResponseEntity.ok(convertToBookingDTO(booking));
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
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(bookingDTO,Booking.class);
    }
    private BookingDTO convertToBookingDTO(Booking booking){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(booking,BookingDTO.class);
    }


}
