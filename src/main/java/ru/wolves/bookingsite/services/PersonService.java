package ru.wolves.bookingsite.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.wolves.bookingsite.exceptions.PersonExceptions.NotValidPhoneNumberException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonAlreadyExistException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonNotFoundException;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.models.dto.AuthenticationResponse;
import ru.wolves.bookingsite.models.dto.PersonAuthenticationRequest;
import ru.wolves.bookingsite.models.dto.RegisterRequest;

import java.io.IOException;
import java.util.List;

public interface PersonService {
    Person findPerson(Long id) throws PersonNotFoundException;
    List<Person> findAll();
    Person updatePerson(Long id, Person person) throws PersonNotFoundException;
    AuthenticationResponse authPersonByPhone(String phoneNumber) throws PersonNotFoundException, NotValidPhoneNumberException;
    AuthenticationResponse registerPerson(RegisterRequest registerRequest) throws NotValidPhoneNumberException, PersonAlreadyExistException;
    AuthenticationResponse authPersonByEmail(PersonAuthenticationRequest request) throws PersonNotFoundException;
    void deletePerson(Long id) throws PersonNotFoundException;
    Person findPersonByPhone(String phone) throws PersonNotFoundException;
    Person findPersonByEmail(String email) throws PersonNotFoundException;
    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException;
}
