package ru.wolves.bookingsite.exceptions.PersonExceptions;

public class PersonNotFoundException extends Exception{
    public PersonNotFoundException(String message) {
        super(message);
    }
}
