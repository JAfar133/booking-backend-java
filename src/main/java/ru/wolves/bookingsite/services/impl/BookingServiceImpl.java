package ru.wolves.bookingsite.services.impl;



import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.wolves.bookingsite.exceptions.PersonExceptions.PersonNotFoundException;
import ru.wolves.bookingsite.exceptions.bookingExceptions.BookingNotFoundException;
import ru.wolves.bookingsite.models.Booking;
import ru.wolves.bookingsite.models.Person;
import ru.wolves.bookingsite.models.RoomHall;
import ru.wolves.bookingsite.repositories.BookingRepo;
import ru.wolves.bookingsite.repositories.PersonRepo;
import ru.wolves.bookingsite.services.BookingService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepo bookingRepo;
    private final PersonRepo personRepo;
    private static org.apache.log4j.Logger log = Logger.getLogger(BookingServiceImpl.class);

    @Autowired
    public BookingServiceImpl(BookingRepo bookingRepo, PersonRepo personRepo) {
        this.bookingRepo = bookingRepo;
        this.personRepo = personRepo;
    }

    public List<Booking> findAllByRoomHall(RoomHall roomHall){
        return bookingRepo.findAllByPlace(roomHall);
    }
    @Override
    public List<Booking> findAllBooking() {
        return bookingRepo.findAll();
    }

    public Booking findBooking(Long id) throws BookingNotFoundException {
        Optional<Booking> booking = bookingRepo.findById(id);
        if(!booking.isPresent())
            throw new BookingNotFoundException("Бронь с таким id не существует");
        return booking.get();
    }

    @Transactional
    public void savePersonWithBooking(Booking booking) throws PersonNotFoundException {
        Person person = booking.getCustomer();
        if(!personRepo.findByPhoneNumber(person.getPhoneNumber()).isPresent()){
            throw new PersonNotFoundException("Пользователь с таким номером телефона не зарегистрирован");
        }
        booking.setBookedAt(new Date());
        if(person.getBookingList()==null)
            person.setBookingList(new ArrayList<>());
        person.getBookingList().add(booking);
        personRepo.save(person);
        booking.setCustomer(person);
        bookingRepo.save(booking);
    }

    private String getLastNameInitials(Person person){
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
    public void saveBooking(Booking booking){
        bookingRepo.save(booking);
        log.info("Booking with id = "+booking.getId()+" was saved successfully");
    }

    @Override
    @Transactional
    public void deleteBooking(Booking booking) throws BookingNotFoundException {
        deleteBooking(booking.getId());
        log.info("Booking with id = "+booking.getId()+" was deleted successfully");
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) throws BookingNotFoundException {
        Booking booking = findBooking(id);
        if(booking==null){
            log.info("Booking with id = "+id+" not found");
            throw new BookingNotFoundException("Бронь с таким id не существует");
        }
        bookingRepo.delete(booking);
        log.info("Booking with id = "+id+" was deleted successfully");
    }

    @Override
    @Scheduled(cron="0 0 3 * * ?") // Каждый день в 3:00
    @Transactional
    public void deleteOldBooking() {
        log.info(LocalDateTime.now()+": Start scanning old booking...");
        LocalDate today = LocalDate.now();
        findAllBooking().stream().forEach(x-> {
            LocalDate date = x.getDate();
            if(date.isBefore(today)) {
                try {
                    deleteBooking(x);
                } catch (BookingNotFoundException e) {
                    log.info("Booking with id = "+x.getId()+" not found");
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
        log.info("Booking with id = "+id+" was updated successfully");
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
    public List<Booking> findAllBookingWithPlaceAndDate(RoomHall roomHall, LocalDate date) {
        return bookingRepo.findAllByPlaceAndDate(Sort.by("timeStart"), roomHall, date);
    }
}
