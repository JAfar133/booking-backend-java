package ru.wolves.bookingsite.services;

import ru.wolves.bookingsite.exceptions.PlaceIsNotFoundException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.PlaceIsNotFreeException;
import ru.wolves.bookingsite.models.Booking;
import ru.wolves.bookingsite.models.RoomHall;

import java.util.List;

public interface RoomHallService {
    RoomHall findRoom(Long id) throws PlaceIsNotFreeException, PlaceIsNotFoundException;
    RoomHall findRoom(String name);
    List<RoomHall> findAllRoomHall();
    void saveRoom(RoomHall roomHall);

    RoomHall updateRoom(Long id, RoomHall roomhall) throws PlaceIsNotFoundException;

    void deleteRoomHall(Long id) throws PlaceIsNotFoundException;
    List<RoomHall> findFreeRooms(Booking booking);
    RoomHall findByEngName(String engName);

}
