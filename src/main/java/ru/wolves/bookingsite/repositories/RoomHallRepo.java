package ru.wolves.bookingsite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.models.RoomHall;

import java.util.List;
import java.util.Optional;

public interface RoomHallRepo extends JpaRepository<RoomHall,Long> {
    Optional<RoomHall> findByName(String name);
    Optional<RoomHall> findByEngName(String engName);
}
