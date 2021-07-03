package it.dbortoluzzi.tuttiapposto.server.repositories;

import com.google.cloud.firestore.Firestore;
import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import it.dbortoluzzi.tuttiapposto.server.models.Room;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepository extends AbstractFirestoreRepository<Room> {
    protected RoomRepository(Firestore firestore) {
        super(firestore, "Rooms");
    }
}