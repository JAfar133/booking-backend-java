package ru.wolves.bookingsite.exceptions;

public class WrongOperationException extends Exception{
    public WrongOperationException(String message) {
        super(message);
    }
}
