package ru.wolves.bookingsite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.wolves.bookingsite.models.Person;

import java.util.Optional;


public interface PersonRepo extends JpaRepository<Person, Long> {
    Optional<Person> findByPhoneNumber(String phone);
}
