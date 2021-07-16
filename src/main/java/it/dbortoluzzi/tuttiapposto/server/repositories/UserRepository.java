package it.dbortoluzzi.tuttiapposto.server.repositories;

import com.google.cloud.firestore.Firestore;
import it.dbortoluzzi.tuttiapposto.server.models.Table;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository extends AbstractFirestoreRepository<Table> {
    protected UserRepository(Firestore firestore) {
        super(firestore, "Users");
    }

    @Override
    public Optional<String> save(Table model) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean delete(String documentId) {
        throw new UnsupportedOperationException("not implemented");
    }
}