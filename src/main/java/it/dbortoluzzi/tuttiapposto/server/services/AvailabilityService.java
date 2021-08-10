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
    BookingService bookingService;

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
        Company company = companyOpt.get();
        Assert.isTrue(company.getActive(), "company is not active");
        List<Booking> existingBookings = bookingService.getBookingsBy(companyId, buildingIdOpt, roomIdOpt, startDate, endDate);
        // TODO: uncomment the test optionally
//        Assert.isTrue(new Date().before(endDate) || new Date().equals(endDate), "endDate after now");

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

        return availabilityResponseDtos.stream().sorted(Comparator.comparingInt(AvailabilityResponseDto::getAvailability).reversed()).collect(Collectors.toList());
    }
}