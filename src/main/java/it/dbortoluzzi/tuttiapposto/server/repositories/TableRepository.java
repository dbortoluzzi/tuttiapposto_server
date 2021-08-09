package it.dbortoluzzi.tuttiapposto.server.repositories;

import com.google.cloud.firestore.Firestore;
import it.dbortoluzzi.tuttiapposto.server.models.Table;
import it.dbortoluzzi.tuttiapposto.server.utils.CommonQueriesBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class TableRepository extends AbstractFirestoreRepository<Table> {
    protected TableRepository(Firestore firestore) {
        super(firestore, "Tables");
    }

    public Integer maxCapacityOf(String roomId) throws ExecutionException, InterruptedException {
        List<Table> tableList = findByQuery(CommonQueriesBuilder
                .newBuilder(collectionReference)
                .room(roomId)
                .active(true)
                .buildQuery()
        );
        return tableList.stream().map(Table::getMaxCapacity).reduce(0, Integer::sum);
    }
}