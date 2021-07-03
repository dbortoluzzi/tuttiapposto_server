package it.dbortoluzzi.tuttiapposto.server.repositories;

import com.google.cloud.firestore.Firestore;
import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import it.dbortoluzzi.tuttiapposto.server.models.Building;
import org.springframework.stereotype.Repository;

@Repository
public class BuildingRepository extends AbstractFirestoreRepository<Building> {
    protected BuildingRepository(Firestore firestore) {
        super(firestore, "Buildings");
    }
}