package it.dbortoluzzi.tuttiapposto.server.controllers;

import it.dbortoluzzi.tuttiapposto.server.controllers.dto.AvailabilityResponseDto;
import it.dbortoluzzi.tuttiapposto.server.controllers.dto.BookingRequestDto;
import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import it.dbortoluzzi.tuttiapposto.server.repositories.BookingRepository;
import it.dbortoluzzi.tuttiapposto.server.services.AvailabilityService;
import it.dbortoluzzi.tuttiapposto.server.services.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class BookingController {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingService bookingService;

    @Autowired
    AvailabilityService availabilityService;

    @GetMapping("/api/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = null;
        try {
            bookings = bookingRepository.findAll();
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error reading allBookings", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/bookings/filtered")
    public ResponseEntity<List<Booking>> getBookings(@RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Get bookings for {}", bookingRequestDto);
        List<Booking> bookings = null;
        try {
            bookings = bookingService.getBookingsFiltered(
                    bookingRequestDto.getCompanyId(),
                    Optional.ofNullable(bookingRequestDto.getBuildingId()),
                    Optional.ofNullable(bookingRequestDto.getRoomId()),
                    Optional.ofNullable(bookingRequestDto.getUserId()),
                    bookingRequestDto.getStartDate(),
                    bookingRequestDto.getEndDate()
            );
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error reading getBookings", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/bookings/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable("id") String id) {

        Optional<Booking> booking = bookingRepository.get(id);

        return booking.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/api/bookings")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        log.info("Requesting booking for {}", booking);
        if (isBookingAvailable(booking)) {
            Optional<String> save = bookingRepository.save(booking);
            return save
                    .map(value -> new ResponseEntity<>(new Booking(value, booking.getUserId(), booking.getCompanyId(), booking.getBuildingId(), booking.getRoomId(), booking.getTableId(), booking.getStartDate(), booking.getEndDate()), HttpStatus.CREATED))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        } else {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/bookings/{id}")
    // Attention: the booking argument must be different from saved element can be updated
    public ResponseEntity<String> updateBooking(@PathVariable("id") String id, @RequestBody Booking booking) {
        Optional<Booking> optionalBooking = bookingRepository.get(id);
        if (optionalBooking.isPresent()) {
            if (isBookingAvailable(optionalBooking.get())) {
                booking.setUID(id);
                Optional<String> update = bookingRepository.save(booking);
                return update.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
            } else {
                return new ResponseEntity<>("booking is not more available", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("booking doesn't exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/api/bookings/{id}")
    public ResponseEntity<Boolean> deleteBooking(@PathVariable("id") String id) {
        return new ResponseEntity<>(bookingRepository.delete(id), HttpStatus.OK);
    }

    private boolean isBookingAvailable(Booking booking) {
        try {
            List<AvailabilityResponseDto> availableTables = availabilityService.findAvailableTables(
                    booking.getCompanyId(),
                    Optional.of(booking.getBuildingId()),
                    Optional.of(booking.getRoomId()),
                    booking.getStartDate(),
                    booking.getEndDate(),
                    Optional.empty()
            );
            return availableTables.size() > 0;
        } catch (Exception e) {
            log.error("Availability error for booking {}", booking.toString(), e);
            return false;
        }
    }
}