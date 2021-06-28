package it.dbortoluzzi.tuttiapposto.server;

import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import it.dbortoluzzi.tuttiapposto.server.repositories.BookingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Optional;

@SpringBootTest
class SpringFirebaseApplicationTests {

    public static final String NEW_FAKE_COMPANY_ID = "NEW_FAKE_COMPANY_ID";
    @Autowired
    BookingRepository bookingRepository;

    @Test
    void contextLoads() {
        Booking booking = new Booking(null, "FAKE_USER_ID", "FAKE_COMPANY_ID");
        Optional<String> save = bookingRepository.save(booking);

        Assert.isTrue(save.isPresent(), "save booking is present");

        String bookingId = save.get();

        Optional<Booking> bookingFetchedOpt = bookingRepository.get(bookingId);
        Assert.isTrue(bookingFetchedOpt.isPresent(), "fetched booking is present");

        Booking bookingFetched = bookingFetchedOpt.get();
        bookingFetched.setCompanyUid(NEW_FAKE_COMPANY_ID);
        bookingRepository.save(bookingFetched);

        Optional<Booking> bookingUpdatedOpt = bookingRepository.get(bookingId);
        Assert.isTrue(bookingUpdatedOpt.isPresent(), "updated booking is present");
        Assert.isTrue(bookingUpdatedOpt.get().getCompanyUid().equals(NEW_FAKE_COMPANY_ID), "updated booking is correct");

        bookingRepository.delete(bookingId);
        Optional<Booking> bookingDeletedOpt = bookingRepository.get(bookingId);
        Assert.isTrue(!bookingDeletedOpt.isPresent(), "updated booking is deleted");
    }

}
