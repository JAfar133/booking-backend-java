package ru.wolves.bookingsite.exceptions.bookingExceptions;

public class BookingNotFoundException extends Exception{
    public BookingNotFoundException(String message) {
        super(message);
    }
}
