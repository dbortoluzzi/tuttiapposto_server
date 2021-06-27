package it.dbortoluzzi.tuttiapposto.server.controllers;

import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import it.dbortoluzzi.tuttiapposto.server.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class BookingController {

    @Autowired
    BookingService bookingService;

    @GetMapping("/api/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/api/bookings/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable("id") String id) throws InterruptedException, ExecutionException {

        Booking booking = bookingService.getBookingById(id);

        if (booking != null) {
            return new ResponseEntity<>(booking, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/bookings")
    public ResponseEntity<String> saveBooking(@RequestBody Booking booking) throws InterruptedException, ExecutionException {
        return new ResponseEntity<>(bookingService.saveBooking(booking), HttpStatus.CREATED);
    }

    @PostMapping("/api/bookings/{id}")
    public ResponseEntity<Boolean> updateBooking(@PathVariable("id") String id, @RequestBody Booking booking) throws InterruptedException, ExecutionException {
        return new ResponseEntity<>(bookingService.updateBooking(id, booking), HttpStatus.OK);
    }

    @DeleteMapping("/api/bookings/{id}")
    public ResponseEntity<Boolean> deleteBooking(@PathVariable("id") String id) {
        return new ResponseEntity<>(bookingService.deleteBooking(id), HttpStatus.OK);
    }
}