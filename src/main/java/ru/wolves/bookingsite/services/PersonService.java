package ru.wolves.bookingsite.services;

import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonNotFoundException;
import ru.wolves.bookingsite.models.Person;

import java.util.List;

public interface PersonService {
    Person findPerson(Long id) throws PersonNotFoundException;
    List<Person> findAll();
    Person updatePerson(Long id, Person person) throws PersonNotFoundException;
    void savePerson(Person person) throws PersonNotFoundException;
    void deletePerson(Long id) throws PersonNotFoundException;
    Person findPersonByPhone(String phone) throws PersonNotFoundException;
}
