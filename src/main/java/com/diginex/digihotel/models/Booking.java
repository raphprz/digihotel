package com.diginex.digihotel.models;

import com.diginex.digihotel.utils.Validate;

import java.time.LocalDate;
import java.util.Objects;

public class Booking {

    private final String guestName;
    private final Integer roomNumber;
    private final LocalDate date;

    public Booking(String guestName, Integer roomNumber, LocalDate date) {
        Validate.isValidString(guestName, "guestName cannot be null or empty");
        Validate.notNull(roomNumber, "roomNumber cannot be null");
        Validate.notNull(date, "date cannot be null");

        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.date = date;
    }

    public String getGuestName() {
        return guestName;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return guestName.equals(booking.guestName) && roomNumber.equals(booking.roomNumber) && date.equals(booking.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guestName, roomNumber, date);
    }

}
