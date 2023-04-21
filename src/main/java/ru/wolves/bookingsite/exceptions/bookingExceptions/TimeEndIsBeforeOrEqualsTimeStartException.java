package ru.wolves.bookingsite.exceptions.bookingExceptions;

public class TimeEndIsBeforeOrEqualsTimeStartException extends Exception{
    public TimeEndIsBeforeOrEqualsTimeStartException(String message) {
        super(message);
    }
}
