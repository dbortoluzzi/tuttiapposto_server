package it.dbortoluzzi.tuttiapposto.server;

import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import it.dbortoluzzi.tuttiapposto.server.models.Table;
import it.dbortoluzzi.tuttiapposto.server.repositories.BookingRepository;
import it.dbortoluzzi.tuttiapposto.server.repositories.CompanyRepository;
import it.dbortoluzzi.tuttiapposto.server.services.AvailabilityService;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

@SpringBootTest
class SpringFirebaseApplicationTests {

    public static final String NEW_FAKE_COMPANY_ID = "NEW_FAKE_COMPANY_ID";

    private static final String COMPANY_ID = "FbF0or0c0NdBphbZcssm";
    private static final String BUILDING_ID = "VTdqvUGCKLWKq0SFkTHx";
    private static final String ROOM_ID = "B29tSJlDqC6J6OG9Jcug";

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALIAN);

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    AvailabilityService availabilityService;

    @Test
    void contextLoads() throws ExecutionException, InterruptedException {
        List<Booking> bookings = bookingRepository.findAll();

        Date startDate = DateUtils.truncate(new Date(), Calendar.HOUR);
        Date endDate = DateUtils.addHours(startDate, 2);
        Booking booking = new Booking(null, "FAKE_USER_ID", "FAKE_COMPANY_ID", "FAKE_BUILDING_ID", "FAKE_ROOM_ID", "FAKE_TABLE_ID", startDate, endDate);
        Optional<String> save = bookingRepository.save(booking);

        Assert.isTrue(save.isPresent(), "save booking is not present");

        String bookingId = save.get();

        Optional<Booking> bookingFetchedOpt = bookingRepository.get(bookingId);
        Assert.isTrue(bookingFetchedOpt.isPresent(), "fetched booking is not present");

        Booking bookingFetched = bookingFetchedOpt.get();
        bookingFetched.setCompanyId(NEW_FAKE_COMPANY_ID);
        bookingFetched.setEndDate(DateUtils.addDays(bookingFetched.getEndDate(), 1));
        bookingRepository.save(bookingFetched);

        Optional<Booking> bookingUpdatedOpt = bookingRepository.get(bookingId);
        Assert.isTrue(bookingUpdatedOpt.isPresent(), "updated booking is not present");
        Assert.isTrue(bookingUpdatedOpt.get().getCompanyId().equals(NEW_FAKE_COMPANY_ID), "updated booking is not correct");

        boolean delete = bookingRepository.delete(bookingId);
        Assert.isTrue(delete, "updated booking is not deleted");
    }

    @Test
    void getAvailability() throws ExecutionException, InterruptedException, ParseException {
        Date startDate = formatter.parse("2021-07-03 09:00:00");
        Date endDate = formatter.parse("2021-07-03 12:00:00");
        List<Table> availableTables = availabilityService.findAvailableTables(COMPANY_ID, Optional.of(BUILDING_ID), Optional.of(ROOM_ID), startDate, endDate);

        Assert.isTrue(availableTables.size() > 0, "not found available table");
    }

}
