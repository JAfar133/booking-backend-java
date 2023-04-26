package ru.wolves.bookingsite.exceptions.PersonExceptions;

public class PersonAlreadyExistException extends Exception{
    public PersonAlreadyExistException(String message) {
        super(message);
    }
}
