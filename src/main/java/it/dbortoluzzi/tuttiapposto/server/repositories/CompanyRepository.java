package it.dbortoluzzi.tuttiapposto.server.repositories;

import com.google.cloud.firestore.Firestore;
import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import it.dbortoluzzi.tuttiapposto.server.models.Company;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyRepository extends AbstractFirestoreRepository<Company> {
    protected CompanyRepository(Firestore firestore) {
        super(firestore, "Companies");
    }
}