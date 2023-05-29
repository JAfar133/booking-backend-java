package ru.wolves.bookingsite.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.wolves.bookingsite.exceptions.bookingExceptions.PlaceIsNotFreeException;
import ru.wolves.bookingsite.exceptions.FieldIsEmptyException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.TimeEndIsBeforeOrEqualsTimeStartException;
import ru.wolves.bookingsite.models.Booking;
import ru.wolves.bookingsite.services.impl.BookingServiceImpl;

@Component
public class BookingValidator {
    private final BookingServiceImpl bookingServiceImpl;
    @Autowired
    public BookingValidator(BookingServiceImpl bookingServiceImpl) {
        this.bookingServiceImpl = bookingServiceImpl;
    }


    public void validate(Booking booking) throws FieldIsEmptyException, TimeEndIsBeforeOrEqualsTimeStartException {
        if(booking.getDate() == null){
            throw new FieldIsEmptyException("Поле `Дата` не может быть пустым");
        }
        if(booking.getTimeStart() == null){
            throw new FieldIsEmptyException("Поле `Начало` не может быть пустым");
        }
        if(booking.getTimeEnd() == null){
            throw new FieldIsEmptyException("Поле `Конец` не может быть пустым");
        }
        if(booking.getPlace() == null){
            throw new FieldIsEmptyException("Поле `Помещение` не может быть пустым");
        }
        if(booking.getTimeStart().getTime() - booking.getTimeEnd().getTime() >= 0){
            throw new TimeEndIsBeforeOrEqualsTimeStartException("Время начала не может быть больше или совпадать с временем окончания");
        }
        placeIsNotFree(booking);

    }

    private void placeIsNotFree(Booking booking){
        bookingServiceImpl.findAllByRoomHall(booking.getPlace()).forEach(booking1 -> {
            if (booking1.getDate().equals(booking.getDate())) {
                if(booking1.getTimeStart().before(booking.getTimeStart())) {
                    if (booking1.getTimeEnd().after(booking.getTimeStart())) {
                        throw new PlaceIsNotFreeException("Помещение занято в это время");
                    }
                } else {
                    if(booking.getTimeEnd().after(booking1.getTimeStart())  )
                        throw new PlaceIsNotFreeException("Помещение занято в это время");
                }
            }
        });
    }
}
