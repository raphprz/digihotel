package com.diginex.digihotel.services;

import com.diginex.digihotel.errors.BookingError;
import com.diginex.digihotel.models.Booking;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HotelServiceTest {

    @Test
    public void getAvailableRooms_ShouldSucceed() {
        HotelService hotelService = new HotelService(5);

        LocalDate date = LocalDate.of(2021, 6, 11);
        Booking booking1 = new Booking("Guest1", 1, date);
        Booking booking2 = new Booking("Guest2", 3, date);

        hotelService.storeBooking(booking1);
        hotelService.storeBooking(booking2);

        assertThat(hotelService.getAvailableRooms(date)).containsExactly(2, 4, 5);
    }

    @Test
    public void getAvailableRooms_ShouldReturnAllRooms_BecauseNoBookingOnThatDate() {
        HotelService hotelService = new HotelService(5);

        LocalDate date = LocalDate.of(2021, 6, 11);
        Booking booking1 = new Booking("Guest1", 1, date);

        hotelService.storeBooking(booking1);

        assertThat(hotelService.getAvailableRooms(date.plusDays(1))).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void storeBooking_ShouldThrow_BecauseSameRoomAndDate() {
        HotelService hotelService = new HotelService(5);

        LocalDate date = LocalDate.of(2021, 6, 11);
        Booking booking1 = new Booking("Guest1", 1, date);
        Booking booking2 = new Booking("Guest2", 1, date);

        hotelService.storeBooking(booking1);

        assertThatThrownBy(() -> hotelService.storeBooking(booking2)).isInstanceOf(BookingError.class);
    }

    @Test
    public void storeBooking_ShouldThrow_BecauseRoomDoesNotExist() {
        HotelService hotelService = new HotelService(5);

        LocalDate date = LocalDate.of(2021, 6, 11);
        Booking booking1 = new Booking("Guest1", 6, date);


        assertThatThrownBy(() -> hotelService.storeBooking(booking1)).isInstanceOf(BookingError.class);
    }

    @Test
    public void getBookingsByGuest_ShouldReturnTwoBookings() {
        HotelService hotelService = new HotelService(5);

        Booking booking1 = new Booking("Guest1", 1, LocalDate.of(2021, 6, 11));
        Booking booking2 = new Booking("Guest1", 3, LocalDate.of(2021, 6, 12));
        Booking booking3 = new Booking("Guest2", 3, LocalDate.of(2021, 6, 11));

        hotelService.storeBooking(booking1);
        hotelService.storeBooking(booking2);
        hotelService.storeBooking(booking3);

        assertThat(hotelService.getBookingsByGuest("Guest1")).containsExactly(booking1, booking2);
        assertThat(hotelService.getBookingsByGuest("Guest2")).containsExactly(booking3);
    }

    @Test
    public void storeBooking_ShouldWorkWithMultipleThreads() throws InterruptedException {
        // Note: if you remove the synchronized keywords on the HotelService class methods,
        // you will see some errors here. For example, some booking might not be processed

        ExecutorService service = Executors.newFixedThreadPool(3);
        HotelService hotelService = new HotelService(5);

        Booking booking1 = new Booking("Guest1", 1, LocalDate.of(2021, 6, 11));
        Booking booking2 = new Booking("Guest1", 3, LocalDate.of(2021, 6, 12));
        Booking booking3 = new Booking("Guest2", 3, LocalDate.of(2021, 6, 11));
        List<Booking> bookings = List.of(booking1, booking2, booking3);

        bookings.forEach(booking -> service.submit(() -> hotelService.storeBooking(booking)));
        service.awaitTermination(100, TimeUnit.MILLISECONDS);

        assertThat(hotelService.getBookingsByGuest("Guest1")).containsExactlyInAnyOrder(booking1, booking2);
        assertThat(hotelService.getBookingsByGuest("Guest2")).containsExactlyInAnyOrder(booking3);
    }

}