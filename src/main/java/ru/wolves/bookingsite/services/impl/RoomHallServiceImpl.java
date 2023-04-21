package ru.wolves.bookingsite.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.wolves.bookingsite.exceptions.PlaceIsNotFoundException;
import ru.wolves.bookingsite.models.Booking;
import ru.wolves.bookingsite.models.RoomHall;
import ru.wolves.bookingsite.repositories.BookingRepo;
import ru.wolves.bookingsite.repositories.RoomHallRepo;
import ru.wolves.bookingsite.services.RoomHallService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RoomHallServiceImpl implements RoomHallService {

    private final RoomHallRepo roomHallRepo;
    private final BookingRepo bookingRepo;

    @Autowired
    public RoomHallServiceImpl(RoomHallRepo roomHallRepo, BookingRepo bookingRepo) {
        this.roomHallRepo = roomHallRepo;
        this.bookingRepo = bookingRepo;
    }

    @Override
    @Cacheable(cacheNames = "booking", key = "#id")
    public RoomHall findRoom(Long id) throws PlaceIsNotFoundException {
        Optional<RoomHall> roomHall = roomHallRepo.findById(id);
        if(roomHall.isPresent())
            return roomHall.get();
        else throw new PlaceIsNotFoundException("Room with id = "+id+" wasn't found");
    }

    @Override
    public RoomHall findRoom(String name) {
        Optional<RoomHall> roomHall = roomHallRepo.findByName(name);
        if(roomHall.isPresent())
            return roomHall.get();
        else return null; //TODO
    }

    @Cacheable(cacheNames = "bookinglist")
    public List<RoomHall> findAllRoomHall(){
        return roomHallRepo.findAll();
    }

    @Override
    @Transactional
    public void saveRoom(RoomHall roomHall) {
        roomHallRepo.save(roomHall);
    }

    @Override
    @Transactional
    public RoomHall updateRoom(Long id, RoomHall roomhall) throws PlaceIsNotFoundException {
        Optional<RoomHall> roomHallOptional = roomHallRepo.findById(id);
        if(!roomHallOptional.isPresent()) throw new PlaceIsNotFoundException("Room with id = "+id+" wasn't found");

        RoomHall existingRoomHall = roomHallOptional.get();
        roomhall.setId(id);
        existingRoomHall = roomHallRepo.saveAndFlush(roomhall);
        return existingRoomHall;
    }

    @Override
    @Transactional
    public void deleteRoomHall(Long id) throws PlaceIsNotFoundException {
        Optional<RoomHall> roomHall = roomHallRepo.findById(id);
        if(roomHall.isPresent())
            roomHallRepo.delete(roomHall.get());
        else throw new PlaceIsNotFoundException("Room with id = "+id+" was not found");
    }

    public List<RoomHall> findFreeRooms(Booking booking){
        List<Booking> bookings = bookingRepo.findAllByDate(booking.getDate());
        List<RoomHall> rooms = findAllRoomHall();
        bookings.forEach(b -> {
            if((b.getTimeStart().getTime() <= booking.getTimeStart().getTime()
                    && booking.getTimeEnd().getTime() > b.getTimeStart().getTime()
                    )|| (b.getTimeStart().getTime() > booking.getTimeStart().getTime()
                    && booking.getTimeEnd().getTime() > b.getTimeStart().getTime())){
                rooms.remove(b.getPlace());
            }
        });
        return rooms;
    }

    @Override
    public RoomHall findByEngName(String engName) {
        Optional<RoomHall> roomHall = roomHallRepo.findByEngName(engName);
        if(roomHall.isPresent()) return roomHall.get();
        else return null; //TODO
    }
}
