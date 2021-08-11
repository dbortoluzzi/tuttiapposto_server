package it.dbortoluzzi.tuttiapposto.server.services;

import it.dbortoluzzi.tuttiapposto.server.controllers.dto.AvailabilityResponseDto;
import it.dbortoluzzi.tuttiapposto.server.models.*;
import it.dbortoluzzi.tuttiapposto.server.repositories.*;
import it.dbortoluzzi.tuttiapposto.server.utils.CommonQueriesBuilder;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    AvailabilityReportRepository availabilityReportRepository;

    public List<AvailabilityResponseDto> findAvailableTables(String companyId, Optional<String> buildingIdOpt, Optional<String> roomIdOpt, Date startDate, Date endDate, Optional<String> userIdOpt) throws ExecutionException, InterruptedException {
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

        final Map<String, List<AvailabilityReport>> mapAvailabilityReportByTableId = availabilityReportRepository.getAvailabilityReportByTableId(companyId, startDate, endDate);
        Map<String, List<Booking>> bookingsByTables = existingBookings.stream().collect(groupingBy(Booking::getTableId));

        // get availability for table already booked
        List<AvailabilityResponseDto> tableAvailableAlreadyBooked = bookingsByTables.entrySet()
                .stream()
                .map(t -> {
                    Table table = tableMap.get(t.getKey());
                    Assert.isTrue(table != null, "table "+t.getKey()+" not found");
                    return createAvailabilityResponseDto(startDate, endDate, userIdOpt, mapAvailabilityReportByTableId, table, Table.computeRealCapacity(table, company) - (t.getValue().size()));
                })
                .filter(entry -> entry.getAvailability() >= 1)
                .collect(Collectors.toList());

        // get availability for table without bookings
        List<AvailabilityResponseDto> tableAvailableWithoutBookings = tableMap.entrySet()
                .stream()
                .filter(t -> !bookingsByTables.containsKey(t.getKey()))
                .map(Map.Entry::getValue)
                .map(t -> createAvailabilityResponseDto(startDate, endDate, userIdOpt, mapAvailabilityReportByTableId, t, Table.computeRealCapacity(t, company)))
                .collect(Collectors.toList());

        List<AvailabilityResponseDto> availabilityResponseDtos = new ArrayList<>();
        availabilityResponseDtos.addAll(tableAvailableAlreadyBooked);
        availabilityResponseDtos.addAll(tableAvailableWithoutBookings);

        return availabilityResponseDtos.stream().sorted(Comparator.comparingInt(AvailabilityResponseDto::getAvailability).reversed()).collect(Collectors.toList());
    }

    private AvailabilityResponseDto createAvailabilityResponseDto(Date startDate, Date endDate, Optional<String> userIdOpt, Map<String, List<AvailabilityReport>> mapAvailabilityReportByTableId, Table table, int avalability) {
        List<AvailabilityReport> availabilityReports = extractAvailabilityReportsByTableId(mapAvailabilityReportByTableId, table);
        boolean alreadyReportedByUser = isAlreadyReportedByUser(userIdOpt, availabilityReports);
        return new AvailabilityResponseDto(table, avalability, startDate, endDate, availabilityReports.size() > 0, alreadyReportedByUser);
    }

    private List<AvailabilityReport> extractAvailabilityReportsByTableId(Map<String, List<AvailabilityReport>> mapAvailabilityReportByTableId, Table t) {
        return mapAvailabilityReportByTableId.getOrDefault(t.getUID(), new ArrayList<>());
    }

    private boolean isAlreadyReportedByUser(Optional<String> userIdOpt, List<AvailabilityReport> availabilityReports) {
        return availabilityReports.stream().anyMatch(a -> a.getUserId().equals(userIdOpt.orElse("UNKNOWN")));
    }
}