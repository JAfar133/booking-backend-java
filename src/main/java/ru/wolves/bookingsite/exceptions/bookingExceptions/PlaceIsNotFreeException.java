package ru.wolves.bookingsite.exceptions.bookingExceptions;

public class PlaceIsNotFreeException extends RuntimeException{
    public  PlaceIsNotFreeException(String message){
        super(message);
    }
}
