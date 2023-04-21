package ru.wolves.bookingsite.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonNotFoundException;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.repositories.BookingRepo;
import ru.wolves.bookingsite.repositories.PersonRepo;
import ru.wolves.bookingsite.services.PersonService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {
    private final PersonRepo personRepo;
    private final BookingRepo bookingRepo;

    @Autowired
    public PersonServiceImpl(PersonRepo personRepo, BookingRepo bookingRepo) {
        this.personRepo = personRepo;
        this.bookingRepo = bookingRepo;
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
        else throw new PersonNotFoundException("Person with phone = "+ phone +" wasn't found");

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
    public void savePerson(Person person) throws PersonNotFoundException {
        Optional<Person> personOptional = personRepo.findByPhoneNumber(person.getPhoneNumber());
        if(personOptional.isPresent()) {
            updatePerson(personOptional.get().getId(), person);
        }
        else personRepo.save(person);
    }

    @Override
    @Transactional
    public void deletePerson(Long id) throws PersonNotFoundException {
        personRepo.delete(findPerson(id));
    }
}
