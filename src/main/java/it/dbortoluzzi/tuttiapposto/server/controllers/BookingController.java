package it.dbortoluzzi.tuttiapposto.server.controllers;

import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import it.dbortoluzzi.tuttiapposto.server.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class BookingController {

    @Autowired
    BookingRepository bookingRepository;

    @GetMapping("/api/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingRepository.retrieveAll();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/api/bookings/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable("id") String id) {

        Optional<Booking> booking = bookingRepository.get(id);

        return booking.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/api/bookings")
    public ResponseEntity<String> saveBooking(@RequestBody Booking booking) {
        Optional<String> save = bookingRepository.save(booking);
        return save.map(value -> new ResponseEntity<>(value, HttpStatus.CREATED)).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @PostMapping("/api/bookings/{id}")
    public ResponseEntity<String> updateBooking(@PathVariable("id") String id, @RequestBody Booking booking) {
        booking.setId(id);
        Optional<String> update = bookingRepository.save(booking);
        return update.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping("/api/bookings/{id}")
    public ResponseEntity<Boolean> deleteBooking(@PathVariable("id") String id) {
        return new ResponseEntity<>(bookingRepository.delete(id), HttpStatus.OK);
    }
}