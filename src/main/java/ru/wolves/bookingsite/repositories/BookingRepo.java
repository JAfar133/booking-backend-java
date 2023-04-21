package ru.wolves.bookingsite.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.wolves.bookingsite.models.Booking;
import ru.wolves.bookingsite.models.RoomHall;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepo extends JpaRepository<Booking,Long> {
    List<Booking> findAllByPlace(RoomHall roomHall);
    List<Booking> findAllByDate(LocalDate date);
    List<Booking> findAllByConfirmedIsFalse(Sort sort);
    List<Booking> findAllByConfirmedIsTrue();
    List<Booking> findAllByPlaceAndDate(Sort sort, RoomHall roomHall, LocalDate date);
}
