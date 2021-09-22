package it.dbortoluzzi.tuttiapposto.server.services;

import it.dbortoluzzi.tuttiapposto.server.controllers.dto.OccupationByDateResponseDto;
import it.dbortoluzzi.tuttiapposto.server.controllers.dto.OccupationByElementResponseDto;
import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import it.dbortoluzzi.tuttiapposto.server.models.Company;
import it.dbortoluzzi.tuttiapposto.server.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
public class StatisticsService {
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

    public List<OccupationByDateResponseDto> getOccupationStatsPerHour(String companyId, Date startDate, Date endDate) throws ExecutionException, InterruptedException {
        Optional<Company> companyOpt = companyRepository.get(companyId);
        Assert.isTrue(companyOpt.isPresent(), "company doesn't exist");
        Company company = companyOpt.get();
        Assert.isTrue(company.getActive(), "company is not active");

        List<OccupationByDateResponseDto> occupationByDateResponseDtoList = new ArrayList<>();
        List<Booking> bookings = bookingService.getBookingsBy(companyId, Optional.empty(), Optional.empty(), Optional.empty(), startDate, endDate);
        List<Date> hoursBetween = findHoursBetween(startDate, endDate).stream().sorted().collect(Collectors.toList());
        Map<String, List<Date>> hourOccupationMap = bookings.stream()
                .map(b -> findHoursBetween(b.getStartDate(), b.getEndDate()))
                .flatMap(Collection::stream)
                .collect(groupingBy(Date::toString));

        for (Date occupationHour: hoursBetween) {
            List<Date> occupation = hourOccupationMap.get(occupationHour.toString());
            if (occupation != null) {
                occupationByDateResponseDtoList.add(new OccupationByDateResponseDto(occupationHour, (long) occupation.size()));
            } else {
                occupationByDateResponseDtoList.add(new OccupationByDateResponseDto(occupationHour, 0L));
            }
        }

        return occupationByDateResponseDtoList;
    }

    public List<OccupationByElementResponseDto> getOccupationStatsPerRoom(String companyId, Date startDate, Date endDate) throws ExecutionException, InterruptedException {
        Optional<Company> companyOpt = companyRepository.get(companyId);
        Assert.isTrue(companyOpt.isPresent(), "company doesn't exist");
        Company company = companyOpt.get();
        Assert.isTrue(company.getActive(), "company is not active");

        List<OccupationByElementResponseDto> occupationByDateResponseDtoList = new ArrayList<>();
        List<Booking> bookings = bookingService.getBookingsBy(companyId, Optional.empty(), Optional.empty(), Optional.empty(), startDate, endDate);
        Map<String, Long> roomMap = bookings.stream()
                .map(Booking::getRoomId)
                .collect(groupingBy(
                        Function.identity(), Collectors.counting()
                ));
        return roomMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> {
                    Double roomOccupation = Double.MAX_VALUE;
                    try {
                        Integer maxCapacityOf = tableRepository.maxCapacityOf(e.getKey());
                        roomOccupation = e.getValue()/(double)maxCapacityOf;
                    } catch (Exception ex) {
                        log.error("error retrieving maxCapacityOf room with id = {}", e.getKey());
                    }
                    return new OccupationByElementResponseDto(e.getKey(), e.getValue(), roomOccupation);
                })
                .collect(Collectors.toList());
    }

    private List<Date> findHoursBetween(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();
        Calendar startDateTmp = Calendar.getInstance();
        startDateTmp.setTime(startDate);
        startDateTmp.set(Calendar.MINUTE, 0);
        startDateTmp.set(Calendar.SECOND, 0);
        startDateTmp.set(Calendar.MILLISECOND, 0);

        while (startDateTmp.getTime().before(endDate)) {
            dates.add(startDateTmp.getTime());
            startDateTmp.set(Calendar.HOUR_OF_DAY, startDateTmp.get(Calendar.HOUR_OF_DAY)+1);
        }
        return dates;
    }
}