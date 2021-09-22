package it.dbortoluzzi.tuttiapposto.server.services;

import com.google.cloud.firestore.Query;
import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import it.dbortoluzzi.tuttiapposto.server.models.Building;
import it.dbortoluzzi.tuttiapposto.server.models.Company;
import it.dbortoluzzi.tuttiapposto.server.models.Room;
import it.dbortoluzzi.tuttiapposto.server.repositories.*;
import it.dbortoluzzi.tuttiapposto.server.utils.CommonQueriesBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class BookingService {
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

    public List<Booking> getBookingsFiltered(String companyId, Optional<String> buildingIdOpt, Optional<String> roomIdOpt, Optional<String> userIdOpt, Date startDate, Date endDate) throws ExecutionException, InterruptedException {
        return getBookingsBy(companyId, buildingIdOpt, roomIdOpt, userIdOpt, startDate, endDate);
    }

    protected List<Booking> getBookingsBy(String companyId, Optional<String> buildingIdOpt, Optional<String> roomIdOpt, Optional<String> userIdOpt, Date startDate, Date endDate) throws ExecutionException, InterruptedException {
        Optional<Company> companyOpt = companyRepository.get(companyId);
        Assert.isTrue(companyOpt.isPresent(), "company doesn't exist");
        Assert.isTrue(companyOpt.get().getActive(), "company is not active");

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
        if (endDate != null) {
            Date endDateTruncated = DateUtils.addDays(DateUtils.truncate(endDate, Calendar.DATE), 1);
            while (dateToSearch.before(endDateTruncated)) {
                bookingsToCheck.addAll(bookingRepository.findByQuery(
                        CommonQueriesBuilder
                                .newBuilder(bookingRepository.collectionReference)
                                .company(companyId)
                                .building(buildingIdOpt)
                                .room(roomIdOpt)
                                .user(userIdOpt)
                                .buildQuery()
                                .whereArrayContains("days", dateToSearch)
                                .orderBy("startDate", Query.Direction.ASCENDING)
                        )
                );
                dateToSearch = DateUtils.addDays(dateToSearch, 1);
            }
        } else {
            bookingsToCheck.addAll(bookingRepository.findByQuery(
                    CommonQueriesBuilder
                            .newBuilder(bookingRepository.collectionReference)
                            .company(companyId)
                            .building(buildingIdOpt)
                            .room(roomIdOpt)
                            .buildQuery()
                            .whereGreaterThanOrEqualTo("startDate", dateToSearch)
                            .orderBy("startDate", Query.Direction.ASCENDING)
            ));
        }
        if (bookingsToCheck.isEmpty()) {
            return bookingsToCheck;
        } else {
            return bookingsToCheck
                    .stream()
                    .filter(b -> (endDate == null || b.getStartDate().before(endDate) || b.getStartDate().equals(endDate)) && (startDate.before(b.getEndDate()) || startDate.equals(b.getEndDate())))
                    .collect(Collectors.toList());
        }
    }
}