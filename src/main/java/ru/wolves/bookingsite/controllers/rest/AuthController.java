package ru.wolves.bookingsite.controllers.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.wolves.bookingsite.exceptions.FieldIsEmptyException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.NotValidPhoneNumberException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonAlreadyExistException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonNotFoundException;
import ru.wolves.bookingsite.models.dto.AuthenticationResponse;
import ru.wolves.bookingsite.models.dto.PersonAuthenticationRequest;
import ru.wolves.bookingsite.models.dto.RegisterRequest;
import ru.wolves.bookingsite.services.impl.PersonServiceImpl;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Value("${CLIENT_URL}")
    private String CLIENT_URL;
    private final PersonServiceImpl personService;

    @Autowired
    public AuthController(PersonServiceImpl personService) {
        this.personService = personService;

    }
    @GetMapping("/redirect")
    public void redirect(@RequestParam("access_token") String accessToken,
                         @RequestParam("refresh_token") String refreshToken, HttpServletResponse response) throws IOException {
        StringBuilder stringBuilder = new StringBuilder(CLIENT_URL);
        stringBuilder
                .append("/oauth?access_token=")
                .append(accessToken)
                .append("&refresh_token=")
                .append(refreshToken);

        response.sendRedirect(stringBuilder.toString());
    }
    @PostMapping("/login/email")
    public ResponseEntity<AuthenticationResponse> emailLogin(
            @RequestBody PersonAuthenticationRequest request) throws PersonNotFoundException {

        return ResponseEntity.ok(personService.authPersonByEmail(request));
    }
    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request) throws NotValidPhoneNumberException, PersonAlreadyExistException {

        return ResponseEntity.ok(personService.registerPerson(request));

    }
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        personService.refreshToken(request,response);
    }

    @ExceptionHandler({
            PersonNotFoundException.class, FieldIsEmptyException.class,
            NotValidPhoneNumberException.class, PersonAlreadyExistException.class
    })
    private ResponseEntity<?> handler(Exception e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
