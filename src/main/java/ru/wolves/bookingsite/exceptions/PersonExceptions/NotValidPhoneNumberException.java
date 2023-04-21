package ru.wolves.bookingsite.exceptions.PersonExceptions;

public class NotValidPhoneNumberException extends Exception{
    public NotValidPhoneNumberException(String message) {
        super(message);
    }
}
