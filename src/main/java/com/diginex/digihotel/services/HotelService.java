package com.diginex.digihotel.services;

import com.diginex.digihotel.errors.BookingError;
import com.diginex.digihotel.models.Booking;
import com.diginex.digihotel.utils.Validate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HotelService {

    private final Integer numberOfRooms;
    private final List<Booking> bookings;

    public HotelService(Integer numberOfRooms) {
        Validate.notNull(numberOfRooms, "numberOfRooms cannot be null");
        Validate.isTrue(numberOfRooms > 0, "numberOfRooms must be a positive integer: %d", numberOfRooms);

        this.numberOfRooms = numberOfRooms;
        this.bookings = new ArrayList<>();
    }

    public synchronized void storeBooking(Booking booking) {
        Integer roomNumber = booking.getRoomNumber();
        LocalDate date = booking.getDate();

        if (!isRoomNumberValid(booking))
            throw new BookingError(String.format("Room %d does not exist in this hotel!", roomNumber));

        if (isRoomAlreadyBooked(booking))
            throw new BookingError(String.format("Room %d is not available on the date %s!", roomNumber, date));

        bookings.add(booking);
    }

    public synchronized Set<Integer> getAvailableRooms(LocalDate date) {
        Set<Integer> roomsAlreadyBooked = bookings.stream()
                .filter(booking -> booking.getDate().equals(date))
                .map(Booking::getRoomNumber)
                .collect(Collectors.toSet());

        // here we use a stream to create the set of available rooms
        // it's less readable and less efficient than just doing allRooms.removeAll(roomsAlreadyBooked)
        // but removeAll modifies the set, and here the stream solution produces a new set
        // which is better for immutability
        return getAllRooms().stream()
                .filter(room -> !roomsAlreadyBooked.contains(room))
                .collect(Collectors.toSet());
    }

    public synchronized List<Booking> getBookingsByGuest(String guestName) {
        return bookings.stream()
                .filter(booking -> booking.getGuestName().equals(guestName))
                .collect(Collectors.toList());
    }

    private boolean isRoomAlreadyBooked(Booking booking) {
        // A room is already taken if the list of available rooms
        // does not contain an entry for the given date and room number
        return !getAvailableRooms(booking.getDate()).contains(booking.getRoomNumber());
    }

    private boolean isRoomNumberValid(Booking booking) {
        // Since we consider the hotel contains room from 1 to the capacity of the hotel
        // (see getAllRooms below)
        // this makes the room number check very simple
        return booking.getRoomNumber() > 0 && booking.getRoomNumber() <= this.numberOfRooms;
    }

    private Set<Integer> getAllRooms() {
        // in this hotel, we consider rooms to only be a number
        // from 1 to the capacity of the hotel

        return IntStream.rangeClosed(1, this.numberOfRooms)
                .boxed()
                .collect(Collectors.toUnmodifiableSet());
    }

}
