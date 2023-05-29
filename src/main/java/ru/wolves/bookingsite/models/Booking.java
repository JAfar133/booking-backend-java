package ru.wolves.bookingsite.models;


import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @Column(name = "time_start")
    @Temporal(TemporalType.TIME)
    @DateTimeFormat(pattern = "HH:mm")
    private Date timeStart;

    @Column(name = "time_end")
    @Temporal(TemporalType.TIME)
    @DateTimeFormat(pattern = "HH:mm")
    private Date timeEnd;

    @Column(name = "booked_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bookedAt;
    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person customer;

    @ManyToOne
    @JoinColumn(name = "roomhall_id", referencedColumnName = "id")
    private RoomHall place;

    private String comment;

    private boolean confirmed;



    public Booking(Date bookedAt, Person customer) {
        this.bookedAt = bookedAt;
        this.customer = customer;
    }

    public Booking(LocalDate date, Date timeStart, Date timeEnd) {
        this.date = date;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public Booking() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public RoomHall getPlace() {
        return place;
    }

    public void setPlace(RoomHall place) {
        this.place = place;
    }

    public Date getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(Date bookedAt) {
        this.bookedAt = bookedAt;
    }

    public Person getCustomer() {
        return customer;
    }

    public void setCustomer(Person customer) {
        this.customer = customer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id.equals(booking.id) && Objects.equals(date, booking.date) && Objects.equals(timeStart, booking.timeStart) && Objects.equals(timeEnd, booking.timeEnd) && Objects.equals(bookedAt, booking.bookedAt) && Objects.equals(place, booking.place);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, timeStart, timeEnd, bookedAt, place);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", date=" + date +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", bookedAt=" + bookedAt +
                '}';
    }
}
