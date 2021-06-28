package it.dbortoluzzi.tuttiapposto.server.repositories;

import com.google.cloud.firestore.Firestore;
import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepository extends AbstractFirestoreRepository<Booking> {
    protected BookingRepository(Firestore firestore) {
        super(firestore, "Bookings");
    }
}