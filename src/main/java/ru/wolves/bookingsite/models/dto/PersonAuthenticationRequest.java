package ru.wolves.bookingsite.models.dto;

public class PersonAuthenticationRequest {

    private String email;
    private String password;

    public PersonAuthenticationRequest() {
    }

    public PersonAuthenticationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
