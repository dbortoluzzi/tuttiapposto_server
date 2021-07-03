package it.dbortoluzzi.tuttiapposto.server.services;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import it.dbortoluzzi.tuttiapposto.server.models.*;
import it.dbortoluzzi.tuttiapposto.server.repositories.*;
import it.dbortoluzzi.tuttiapposto.server.utils.CommonQueriesBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class AvailabilityService {
    // TODO: remove
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ITALIAN);

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    BuildingRepository buildingRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    TableRepository tableRepository;

    public List<Table> findAvailableTables(String companyId, Optional<String> buildingIdOpt, Optional<String> roomIdOpt, Date startDate, Date endDate) throws ExecutionException, InterruptedException {
        Optional<Company> companyOpt = companyRepository.get(companyId);
        Assert.isTrue(companyOpt.isPresent(), "company doesn't exist");
        Assert.isTrue(companyOpt.get().getActive(), "company is not active");
        Company company = companyOpt.get();

        Optional<Building> buildingOpt = Optional.empty();
        if (buildingIdOpt.isPresent()) {
            buildingOpt = buildingRepository.get(buildingIdOpt.get());
            Assert.isTrue(buildingOpt.isPresent(), "building doesn't exist");
            Assert.isTrue(buildingOpt.get().getActive(), "building is not active");
        }

        Optional<Room> roomOpt = Optional.empty();
        if (roomIdOpt.isPresent()) {
            roomOpt = roomRepository.get(roomIdOpt.get());
            Assert.isTrue(roomOpt.isPresent(), "room doesn't exist");
            Assert.isTrue(roomOpt.get().getActive(), "room is not active");
        }

        List<Booking> bookingsToCheck = new ArrayList<>();
        Date dateToSearch = DateUtils.truncate(startDate, Calendar.DATE);
        Date endDateTruncated = DateUtils.addDays(DateUtils.truncate(endDate, Calendar.DATE), 1);
        while (dateToSearch.before(endDateTruncated)) {
            bookingsToCheck.addAll(bookingRepository.findByQuery(
                    CommonQueriesBuilder
                            .newBuilder(bookingRepository.collectionReference)
                            .company(companyId)
                            .building(buildingIdOpt)
                            .room(roomIdOpt)
                            .buildQuery()
                            .whereArrayContains("days", dateToSearch)
                    )
            );
            dateToSearch = DateUtils.addDays(dateToSearch, 1);
        }

        List<Booking> existingBookings = bookingsToCheck
                .stream()
                .filter(b -> (b.getStartDate().before(endDate) || b.getStartDate().equals(endDate)) && (startDate.before(b.getEndDate()) || startDate.equals(b.getEndDate())))
                .collect(Collectors.toList());

        Map<String, Table> tableMap = new ArrayList<>(tableRepository.findByQuery(CommonQueriesBuilder
                .newBuilder(tableRepository.collectionReference)
                    .company(companyId)
                    .building(buildingIdOpt)
                    .room(roomIdOpt)
                    .active(true)
                .buildQuery()))
                .stream()
                .collect(Collectors.toMap(Table::getId, Function.identity()));

        Map<String, List<Booking>> bookingsByTables = existingBookings.stream().collect(groupingBy(Booking::getTableId));

        List<String> tableAvailableAlreadyBooked = bookingsByTables.entrySet()
                .stream()
                .filter(table -> {
                    float newPercentage = (table.getValue().size() + 1) / (float) tableMap.get(table.getKey()).getMaxCapacity();
                    return newPercentage <= company.getMaxCapacityPercentage();
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> tableWithoutBookings = tableMap.entrySet()
                .stream()
                .filter(t -> !bookingsByTables.containsKey(t.getKey()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return tableMap.entrySet().stream()
                .filter(el -> tableAvailableAlreadyBooked.contains(el.getKey()) || tableWithoutBookings.contains(el.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}