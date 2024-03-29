package ru.wolves.bookingsite.services.impl;


import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PermissionDeniedException;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonNotFoundException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.BookingNotFoundException;
import ru.wolves.bookingsite.models.Booking;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.models.RoomHall;
import ru.wolves.bookingsite.models.dto.BookingDTO;
import ru.wolves.bookingsite.repositories.BookingRepo;
import ru.wolves.bookingsite.repositories.PersonRepo;
import ru.wolves.bookingsite.security.PersonDetails;
import ru.wolves.bookingsite.services.BookingService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepo bookingRepo;
    private final PersonRepo personRepo;
    private final ModelMapper modelMapper;

    private static org.apache.log4j.Logger log = Logger.getLogger(BookingServiceImpl.class);

    @Autowired
    public BookingServiceImpl(BookingRepo bookingRepo, PersonRepo personRepo, ModelMapper modelMapper) {
        this.bookingRepo = bookingRepo;
        this.personRepo = personRepo;
        this.modelMapper = modelMapper;
    }

    public List<Booking> findAllByRoomHall(RoomHall roomHall) {
        return bookingRepo.findAllByPlace(roomHall);
    }

    @Override
    public List<Booking> findAllBooking() {
        return bookingRepo.findAll();
    }

    public Booking findBooking(Long id) throws BookingNotFoundException {
        Optional<Booking> booking = bookingRepo.findById(id);
        if (!booking.isPresent())
            throw new BookingNotFoundException("Бронь с таким id не существует");
        return booking.get();
    }

    @Transactional
    public void savePersonWithBooking(Booking booking) {
        Optional<Person> person = personRepo.findById(booking.getCustomer().getId());
        if (person.isPresent()) {
            Person clientPerson = booking.getCustomer();
            Person savedPerson = person.get();
            if (clientPerson.getLastName() != null) savedPerson.setLastName(clientPerson.getLastName());
            if (clientPerson.getFirstName() != null) savedPerson.setFirstName(clientPerson.getFirstName());
            if (clientPerson.getMiddleName() != null) savedPerson.setMiddleName(clientPerson.getMiddleName());
            if (clientPerson.getPhoneNumber() != null) savedPerson.setPhoneNumber(clientPerson.getPhoneNumber());
            if (clientPerson.getPost() != null) {
                savedPerson.setPost(clientPerson.getPost());
                if (clientPerson.getPost().equals("Студент")) {
                    savedPerson.setInstitute(clientPerson.getInstitute());
                    savedPerson.setCourse(clientPerson.getCourse());
                } else {
                    savedPerson.setStructure(clientPerson.getStructure());
                }
            }
            booking.setBookedAt(new Date());
            if (savedPerson.getBookingList() == null)
                savedPerson.setBookingList(new ArrayList<>());
            savedPerson.getBookingList().add(booking);
            savedPerson.setLastNameAndInitials(getLastNameInitials(savedPerson));
            personRepo.save(savedPerson);
            booking.setCustomer(savedPerson);
            bookingRepo.save(booking);
        }

    }

    @Transactional
    public void deleteBookings(List<BookingDTO> bookingDTOS, PersonDetails personDetails) throws PermissionDeniedException {
        List<Booking> bookings = new ArrayList<>();
        Person person = personRepo.findById(personDetails.getPerson().getId()).orElseThrow();
        bookingDTOS.forEach(b -> bookings.add(convertToBooking(b)));
        if (new HashSet<>(person.getBookingList()).containsAll(bookings)) {
            bookingRepo.deleteAll(bookings);
        } else throw new PermissionDeniedException("You have no permission");
    }

    private String getLastNameInitials(Person person) {
        var lastName = person.getLastName();
        var firstName = person.getFirstName();
        var middleName = person.getMiddleName();
        StringBuffer fio = new StringBuffer(lastName);
        fio.append(" ")
                .append(firstName.charAt(0))
                .append(".")
                .append(middleName.charAt(0)).append(".");
        return fio.toString();
    }

    @Transactional
    public void saveBooking(Booking booking) {
        bookingRepo.save(booking);
        log.info("Booking with id = " + booking.getId() + " was saved successfully");
    }

    @Override
    @Transactional
    public void deleteBooking(Booking booking) throws BookingNotFoundException {
        deleteBooking(booking.getId());
        log.info("Booking with id = " + booking.getId() + " was deleted successfully");
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) throws BookingNotFoundException {
        Booking booking = findBooking(id);
        if (booking == null) {
            log.info("Booking with id = " + id + " not found");
            throw new BookingNotFoundException("Бронь с таким id не существует");
        }
        bookingRepo.delete(booking);
        log.info("Booking with id = " + id + " was deleted successfully");
    }

    @Override
    @Scheduled(cron = "0 0 3 * * ?") // Каждый день в 3:00
    @Transactional
    public void deleteOldBooking() {
        log.info(LocalDateTime.now() + ": Start scanning old booking...");
        LocalDate today = LocalDate.now();
        findAllBooking().stream().forEach(x -> {
            LocalDate date = x.getDate();
            if (date.isBefore(today)) {
                try {
                    deleteBooking(x);
                } catch (BookingNotFoundException e) {
                    log.info("Booking with id = " + x.getId() + " not found");
                }
            }
        });
    }

    @Override
    @Transactional
    public Booking updateBooking(Long id, Booking booking) throws BookingNotFoundException {
        Booking booking1 = findBooking(id);
        booking1.setPlace(booking.getPlace());
        booking1.setConfirmed(booking.isConfirmed());
        booking1.setDate(booking.getDate());
        booking1.setTimeStart(booking.getTimeStart());
        booking1.setTimeEnd(booking.getTimeEnd());
        booking1.setComment(booking.getComment());
        bookingRepo.save(booking1);
        log.info("Booking with id = " + id + " was updated successfully");
        return booking1;
    }

    @Override
    public List<Booking> findAllUnConfirmedBooking() {
        return bookingRepo.findAllByConfirmedIsFalse(Sort.by("bookedAt"));
    }

    @Override
    public List<Booking> findAllConfirmedBooking() {
        return bookingRepo.findAllByConfirmedIsTrue();
    }

    @Override
    public List<Booking> findAllRejectedBooking() {
        return bookingRepo.findAllByRejectedIsTrue();
    }

    @Override
    public List<Booking> findAllUnRejectedBooking() {
        return bookingRepo.findAllByRejectedIsFalse();
    }

    @Override
    public List<Booking> findAllBookingWithPlaceAndDate(RoomHall roomHall, LocalDate date) {
        return bookingRepo.findAllByPlaceAndDate(Sort.by("timeStart"), roomHall, date);
    }

    private Booking convertToBooking(BookingDTO bookingDTO) {
        return modelMapper.map(bookingDTO, Booking.class);
    }

    private BookingDTO convertToBookingDTO(Booking booking) {
        return modelMapper.map(booking, BookingDTO.class);
    }

}
