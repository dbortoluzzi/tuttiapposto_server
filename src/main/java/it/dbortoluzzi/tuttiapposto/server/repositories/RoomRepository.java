package it.dbortoluzzi.tuttiapposto.server.repositories;

import com.google.cloud.firestore.Firestore;
import it.dbortoluzzi.tuttiapposto.server.models.Room;
import it.dbortoluzzi.tuttiapposto.server.utils.CommonQueriesBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class RoomRepository extends AbstractFirestoreRepository<Room> {
    protected RoomRepository(Firestore firestore) {
        super(firestore, "Rooms");
    }
}