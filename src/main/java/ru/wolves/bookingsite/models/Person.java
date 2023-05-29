package ru.wolves.bookingsite.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import ru.wolves.bookingsite.models.enums.PersonRole;
import ru.wolves.bookingsite.security.socialOauth2.AuthProvider;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "person")
public class Person {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "post")
    private String post;

    @Column(name = "phone")
    @Pattern(regexp = "^(\\+7|7|8)?[\\s-]?\\(?[3489][0-9]{2}\\)?[\\s-]?[0-9]{3}[\\s-]?[0-9]{2}[\\s-]?[0-9]{2}$", message = "Номер введен неверно")
    private String phoneNumber;

    @Column(name = "phone_confirmed")
    private boolean phoneNumberConfirmed;

    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;

    @Column(name = "institute")
    private String institute;
    @Column(name = "course")
    private int course;
    @Column(name = "structure")
    private String structure;

    @Column(name = "last_name_and_initials")
    private String lastNameAndInitials;

    @OneToMany(mappedBy = "customer")
    private List<Booking> bookingList;

    @Enumerated(EnumType.STRING)
    private PersonRole role;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(name = "provider_id")
    private String providerId;

    @OneToMany(mappedBy = "person")
    private List<Token> tokens;

    public Person(String firstName, String lastName, String middleName, String post, String phoneNumber, String institute, int course) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.post = post;
        this.phoneNumber = phoneNumber;
        this.institute = institute;
        this.course = course;
    }

    public Person(String firstName, String lastName, String middleName, String post, String phoneNumber, String structure) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.post = post;
        this.phoneNumber = phoneNumber;
        this.structure = structure;
    }

    public AuthProvider getProvider() {

        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Booking> getBookingList() {
        return bookingList;
    }

    public void setBookingList(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    public String getLastNameAndInitials() {
        return lastNameAndInitials;
    }

    public void setLastNameAndInitials(String lastNameAndInitials) {
        this.lastNameAndInitials = lastNameAndInitials;
    }

    public boolean isPhoneNumberConfirmed() {
        return phoneNumberConfirmed;
    }

    public void setPhoneNumberConfirmed(boolean phoneNumberConfirmed) {
        this.phoneNumberConfirmed = phoneNumberConfirmed;
    }

    public Person() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PersonRole getRole() {
        return role;
    }

    public void setRole(PersonRole role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(middleName, person.middleName) && Objects.equals(phoneNumber, person.phoneNumber);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, middleName, phoneNumber);
    }
}
