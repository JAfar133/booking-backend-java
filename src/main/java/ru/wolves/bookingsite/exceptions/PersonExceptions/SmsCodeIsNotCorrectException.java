package ru.wolves.bookingsite.exceptions.PersonExceptions;

public class SmsCodeIsNotCorrectException extends Exception{
    public SmsCodeIsNotCorrectException(String message) {
        super(message);
    }
}
