package ru.wolves.bookingsite.exceptions.PersonExceptions;

public class PermissionDeniedException extends Exception{
    public PermissionDeniedException(String message) {
        super(message);
    }
}
