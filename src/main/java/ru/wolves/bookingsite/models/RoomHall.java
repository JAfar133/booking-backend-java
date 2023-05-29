package ru.wolves.bookingsite.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "roomhall")
public class RoomHall {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotEmpty
    private String name;

    @Column(name = "address")
    @NotEmpty
    private String address;

    @Column(name = "lon")
    private String lon;

    @Column(name = "lat")
    private String lat;

    @JsonIgnore // Исключаем список Booking из сериализации в JSON
    @OneToMany(mappedBy = "place")
    private List<Booking> bookings;

    @Column(name = "eng_name")
    private String engName;

    public RoomHall(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public RoomHall() {
    }

    public String getEngName() {
        return engName;
    }

    public void setEngName(String engName) {
        this.engName = engName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public String toString() {
        return name + " - " + address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomHall roomHall = (RoomHall) o;
        return Objects.equals(id, roomHall.id) && Objects.equals(name, roomHall.name) && Objects.equals(address, roomHall.address) && Objects.equals(lon, roomHall.lon) && Objects.equals(lat, roomHall.lat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, lon, lat);
    }
}
