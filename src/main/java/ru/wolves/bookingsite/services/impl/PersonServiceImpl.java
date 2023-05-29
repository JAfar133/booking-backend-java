package ru.wolves.bookingsite.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.wolves.bookingsite.exceptions.PersonExceptions.NotValidPhoneNumberException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonAlreadyExistException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonNotFoundException;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.models.Token;
import ru.wolves.bookingsite.models.dto.AuthenticationResponse;
import ru.wolves.bookingsite.models.dto.PersonAuthenticationRequest;
import ru.wolves.bookingsite.models.dto.RegisterRequest;
import ru.wolves.bookingsite.models.enums.PersonRole;
import ru.wolves.bookingsite.models.enums.TokenType;
import ru.wolves.bookingsite.repositories.PersonRepo;
import ru.wolves.bookingsite.repositories.TokenRepo;
import ru.wolves.bookingsite.security.JwtService;
import ru.wolves.bookingsite.security.PersonDetails;
import ru.wolves.bookingsite.security.PersonDetailsService;
import ru.wolves.bookingsite.services.PersonService;
import ru.wolves.bookingsite.util.PersonValidator;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {
    private final PersonRepo personRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PersonValidator personValidator;
    private final TokenRepo tokenRepo;
    private final PersonDetailsService personDetailsService;

    @Autowired
    public PersonServiceImpl(PersonRepo personRepo, PasswordEncoder passwordEncoder,
                             JwtService jwtService, AuthenticationManager authenticationManager,
                             PersonValidator personValidator, TokenRepo tokenRepo,
                             PersonDetailsService personDetailsService) {
        this.personRepo = personRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.personValidator = personValidator;
        this.tokenRepo = tokenRepo;
        this.personDetailsService = personDetailsService;
    }

    public Person findPerson(Long id) throws PersonNotFoundException {
        Optional<Person> person = personRepo.findById(id);
        if(person.isPresent())
            return person.get();
        else throw new PersonNotFoundException("Person with id = "+ id +" wasn't found");
    }

    @Override
    public Person findPersonByPhone(String phone) throws PersonNotFoundException {
        Optional<Person> person = personRepo.findByPhoneNumber(phone);
        if(person.isPresent())
            return person.get();
        else throw new PersonNotFoundException("Пользователь с номером "+ phone +" не зарегистрирован");
    }

    @Override
    public Person findPersonByEmail(String email) throws PersonNotFoundException {
        Optional<Person> person = personRepo.findByEmail(email);
        if(person.isPresent())
            return person.get();
        else throw new PersonNotFoundException("Пользователь с почтой = "+ email +" не зарегистрирован");

    }

    @Override
    public List<Person> findAll() {
        return personRepo.findAll();
    }

    @Override
    @Transactional
    public Person updatePerson(Long id, Person person) throws PersonNotFoundException {
        Person updatedPerson = findPerson(id);
        if(person.getLastName()!=null) updatedPerson.setLastName(person.getLastName());
        if(person.getFirstName()!=null) updatedPerson.setFirstName(person.getFirstName());
        if(person.getMiddleName()!=null) updatedPerson.setMiddleName(person.getMiddleName());
        if(person.getPhoneNumber()!=null) updatedPerson.setPhoneNumber(person.getPhoneNumber());
        if(person.getPost()!=null) {
            updatedPerson.setPost(person.getPost());
            if (person.getPost().equals("Студент")) {
                updatedPerson.setInstitute(person.getInstitute());
                updatedPerson.setCourse(person.getCourse());
            }
            else {
                updatedPerson.setStructure(person.getStructure());
            }
        }
        return personRepo.save(updatedPerson);
    }

    @Override
    @Transactional
    public AuthenticationResponse authPersonByPhone(String phoneNumber) throws PersonNotFoundException, NotValidPhoneNumberException {
        personValidator.validatePhoneNumber(phoneNumber);
        Person person = findPersonByPhone(phoneNumber);
        person.setPhoneNumberConfirmed(true);
        personRepo.save(person);
        PersonDetails personDetails = new PersonDetails(person);
        var jwtToken = jwtService.generateToken(personDetails);
        var refreshToken = jwtService.generateRefreshToken(personDetails);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setAccessToken(jwtToken);
        response.setRefreshToken(refreshToken);
        return response;
    }

    @Transactional
    public Person verifyPhoneNumber(Person person, String phoneNumber) throws NotValidPhoneNumberException{
        personValidator.validatePhoneNumber(phoneNumber);
        person.setPhoneNumber(phoneNumber);
        person.setPhoneNumberConfirmed(true);
        personRepo.save(person);
        return person;
    }

    @Override
    @Transactional
    public AuthenticationResponse authPersonByEmail(PersonAuthenticationRequest request) throws PersonNotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );
        Person person = findPersonByEmail(request.getEmail());
        PersonDetails personDetails = new PersonDetails(person);
        var jwtToken = jwtService.generateToken(personDetails);
        var refreshToken = jwtService.generateRefreshToken(personDetails);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setAccessToken(jwtToken);
        response.setRefreshToken(refreshToken);
        return response;
    }
    @Override
    @Transactional
    public AuthenticationResponse registerPerson(RegisterRequest registerRequest) throws NotValidPhoneNumberException, PersonAlreadyExistException {
        personValidator.validatePhoneNumber(registerRequest.getPhoneNumber());
        if(personRepo.findByPhoneNumber(registerRequest.getPhoneNumber()).isPresent())
            throw new PersonAlreadyExistException("Пользователь с таким номером телефона уже зарегистрирован");
        if(personRepo.findByEmail(registerRequest.getEmail()).isPresent())
            throw new PersonAlreadyExistException("Пользователь с такой почтой уже зарегистрирован");

        Person person = new Person();
            person.setPhoneNumber(registerRequest.getPhoneNumber());
            person.setEmail(registerRequest.getEmail());
            person.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            person.setRole(PersonRole.USER);
        Person savedPerson = personRepo.save(person);
        PersonDetails personDetails = new PersonDetails(person);
        var jwtToken = jwtService.generateToken(personDetails);

        var refreshToken = jwtService.generateRefreshToken(personDetails);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setAccessToken(jwtToken);
        response.setRefreshToken(refreshToken);
        return response;
    }

    private void revokeAllPersonTokens(Person person){
        var validTokens = tokenRepo.findAllValidTokensByPerson(person.getId());
        if(validTokens.isEmpty()) return;
        validTokens.forEach(t->{
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepo.saveAll(validTokens);
    }

    private void savePersonToken(Person savedPerson, String jwtToken) {
        var token = new Token();
            token.setPerson(savedPerson);
            token.setToken(jwtToken);
            token.setTokenType(TokenType.BEARER);
            token.setExpired(false);
            token.setRevoked(false);
        tokenRepo.save(token);
    }


    @Override
    @Transactional
    public void deletePerson(Long id) throws PersonNotFoundException {
        personRepo.delete(findPerson(id));
    }

    @Override
    @Transactional
    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String personEmail;
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        refreshToken = authHeader.substring(7);
        personEmail = jwtService.extractUsername(refreshToken);

        if(personEmail !=null){
            PersonDetails personDetails = (PersonDetails) this.personDetailsService.loadUserByUsername(personEmail);
            if(jwtService.isTokenValid(refreshToken, personDetails)) {
                var accessToken = jwtService.generateToken(personDetails);
//                LOGOUT CODE
//                revokeAllPersonTokens(personDetails.getPerson());
//                savePersonToken(personDetails.getPerson(),accessToken);
                var authResponse = new AuthenticationResponse();
                    authResponse.setAccessToken(accessToken);
                    authResponse.setRefreshToken(refreshToken);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

                objectMapper.writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
    @Transactional
    public Person updatePersonEmail(Long id, Person person) throws PersonNotFoundException, PersonAlreadyExistException {
        Person person1 = findPerson(id);
        if(personRepo.findByEmail(person.getEmail()).isPresent()){
            throw new PersonAlreadyExistException("Пользователь с таким email уже существует");
        }
        person1.setEmail(person.getEmail());
        personRepo.save(person1);
        return person1;
    }
}
