package it.dbortoluzzi.tuttiapposto.server.services;

import it.dbortoluzzi.tuttiapposto.server.controllers.dto.AvailabilityResponseDto;
import it.dbortoluzzi.tuttiapposto.server.models.*;
import it.dbortoluzzi.tuttiapposto.server.repositories.*;
import it.dbortoluzzi.tuttiapposto.server.utils.CommonQueriesBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class AvailabilityService {
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

    public List<AvailabilityResponseDto> findAvailableTables(String companyId, Optional<String> buildingIdOpt, Optional<String> roomIdOpt, Date startDate, Date endDate) throws ExecutionException, InterruptedException {
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

        // TODO: add check on working days

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
                .collect(Collectors.toMap(Table::getUID, Function.identity()));

        Map<String, List<Booking>> bookingsByTables = existingBookings.stream().collect(groupingBy(Booking::getTableId));

        // get availability for table already booked
        List<AvailabilityResponseDto> tableAvailableAlreadyBooked = bookingsByTables.entrySet()
                .stream()
                .map(t -> {
                    Table table = tableMap.get(t.getKey());
                    Assert.isTrue(table != null, "table "+t.getKey()+" not found");
                    Integer realCapacity = Table.computeRealCapacity(table, company);
                    return new AvailabilityResponseDto(table, realCapacity - (t.getValue().size()), startDate, endDate);
                })
                .filter(entry -> entry.getAvailability() >= 1)
                .collect(Collectors.toList());

        // get availability for table without bookings
        List<AvailabilityResponseDto> tableAvailableWithoutBookings = tableMap.entrySet()
                .stream()
                .filter(t -> !bookingsByTables.containsKey(t.getKey()))
                .map(Map.Entry::getValue)
                .map(t -> new AvailabilityResponseDto(t, Table.computeRealCapacity(t, company), startDate, endDate))
                .collect(Collectors.toList());

        List<AvailabilityResponseDto> availabilityResponseDtos = new ArrayList<>();
        availabilityResponseDtos.addAll(tableAvailableAlreadyBooked);
        availabilityResponseDtos.addAll(tableAvailableWithoutBookings);

        return availabilityResponseDtos;
    }
}