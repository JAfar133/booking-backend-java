package ru.wolves.bookingsite.util;

import org.springframework.stereotype.Component;
import ru.wolves.bookingsite.exceptions.FieldIsEmptyException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.NotValidPhoneNumberException;
import ru.wolves.bookingsite.models.Person;

import java.util.regex.Pattern;

@Component
public class PersonValidator {

    public void validate(Person person) throws FieldIsEmptyException, NotValidPhoneNumberException {


        if(person.getLastName()==null) {
            throw new FieldIsEmptyException("Поле `Фамилия` не может быть пустым");
        }
        if(person.getFirstName()==null) {
            throw new FieldIsEmptyException("Поле `Имя` не может быть пустым");
        }
        if(person.getMiddleName()==null) {
            throw new FieldIsEmptyException("Поле `Отчество` не может быть пустым");
        }
        if(person.getPost()==null){
            throw new FieldIsEmptyException("Поле `Должность` не может быть пустым");
        }
        if(person.getPost().equals("Студент") && (person.getInstitute()==null || person.getInstitute().isEmpty())){
            throw new FieldIsEmptyException("Поле `Институт` не может быть пустым");
        }
        if(person.getPost().equals("Студент") && (person.getCourse()==0 || person.getCourse()>5)){
            throw new FieldIsEmptyException("Поле `Курс` не может быть пустым");
        }
        if(person.getPost().equals("Работник") && (person.getStructure()==null || person.getStructure().isEmpty())){
            throw new FieldIsEmptyException("Поле `Структура` не может быть пустым");
        }
        validatePhoneNumber(person.getPhoneNumber());
    }
    public void validatePhoneNumber(String phoneNumber) throws NotValidPhoneNumberException {
        if(!Pattern.matches("^(\\+7|7|8| 7)?[\\s-]?\\(?[3489][0-9]{2}\\)?[\\s-]?[0-9]{3}[\\s-]?[0-9]{2}[\\s-]?[0-9]{2}$",phoneNumber)){
            throw new NotValidPhoneNumberException("Номер телефона введен не правильно");
        }
    }
}
