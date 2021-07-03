package it.dbortoluzzi.tuttiapposto.server.repositories;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class BookingRepository extends AbstractFirestoreRepository<Booking> {
    protected BookingRepository(Firestore firestore) {
        super(firestore, "Bookings");
    }

    @Override
    public Optional<String> save(Booking model) {
        model.rebuildDates();
        return super.save(model);
    }
}