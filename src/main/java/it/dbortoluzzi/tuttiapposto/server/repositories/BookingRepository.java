package it.dbortoluzzi.tuttiapposto.server.repositories;

import com.google.cloud.firestore.Firestore;
import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Optional;

@Repository
@Slf4j
public class BookingRepository extends AbstractFirestoreRepository<Booking> {

    private UserRepository userRepository;

    public BookingRepository(Firestore firestore, UserRepository userRepository) {
        super(firestore, "Bookings");
        this.userRepository = userRepository;
    }

    @Override
    public Optional<String> save(Booking model) {
        Assert.isTrue(new Date().before(model.getEndDate()) || new Date().equals(model.getEndDate()), "endDate after now");
        if (userRepository.get(model.getUserId()).isPresent()) {
            model.rebuildDates();
            return super.save(model);
        } else {
            throw new IllegalStateException("no user valid");
        }
    }
}