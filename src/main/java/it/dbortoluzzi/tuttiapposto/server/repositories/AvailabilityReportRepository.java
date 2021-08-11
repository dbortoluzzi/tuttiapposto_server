package it.dbortoluzzi.tuttiapposto.server.repositories;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import it.dbortoluzzi.tuttiapposto.server.models.AvailabilityReport;
import it.dbortoluzzi.tuttiapposto.server.models.Building;
import it.dbortoluzzi.tuttiapposto.server.models.Company;
import it.dbortoluzzi.tuttiapposto.server.utils.CommonQueriesBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class AvailabilityReportRepository extends AbstractFirestoreRepository<AvailabilityReport> {
    protected AvailabilityReportRepository(Firestore firestore) {
        super(firestore, "AvailabilityReports");
    }

    public Map<String, List<AvailabilityReport>> getAvailabilityReportByTableId(String companyId, Date startDate, Date endDate) throws ExecutionException, InterruptedException {
        Date dateToSearch = DateUtils.truncate(startDate, Calendar.DATE);
        List<AvailabilityReport> availabilityReportsToCheck = new ArrayList<>(findByQuery(
                CommonQueriesBuilder
                        .newBuilder(collectionReference)
                        .company(companyId)
                        .buildQuery()
                        .whereGreaterThanOrEqualTo("startDate", dateToSearch)
                        .orderBy("startDate", Query.Direction.ASCENDING)
        ));

        return availabilityReportsToCheck
                .stream()
                .filter(b -> (endDate == null || b.getStartDate().before(endDate) || b.getStartDate().equals(endDate)) && (startDate.before(b.getEndDate()) || startDate.equals(b.getEndDate())))
                .collect(Collectors.groupingBy(AvailabilityReport::getTableId));
    }
}