package it.dbortoluzzi.tuttiapposto.server.services;

import it.dbortoluzzi.tuttiapposto.server.models.Booking;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class BookingService {

    private static final String COLLECTION_BOOKING = "Bookings";

    public String saveBooking(Booking booking) throws InterruptedException, ExecutionException {

        Firestore firestore = FirestoreClient.getFirestore();

        String id = firestore.collection(COLLECTION_BOOKING).document().getId();

        ApiFuture<WriteResult> collectionsApiFuture = firestore
                .collection(COLLECTION_BOOKING)
                .document(id).set(booking);

        collectionsApiFuture.get();

        return id;
    }

    public Booking getBookingById(String id) throws InterruptedException, ExecutionException {

        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore
                .collection(COLLECTION_BOOKING)
                .document(id);

        ApiFuture<DocumentSnapshot> documentSnapshotFuture = documentReference.get();

        DocumentSnapshot documentSnapshot = documentSnapshotFuture.get();

        if (documentSnapshot.exists()) {
            return documentSnapshot.toObject(Booking.class);
        } else {
            return null;
        }
    }

    public List<Booking> getAllBookings() {

        List<Booking> bookings = new ArrayList<>();

        Firestore firestore = FirestoreClient.getFirestore();

        Iterable<DocumentReference> documentReferences = firestore.collection(COLLECTION_BOOKING).listDocuments();

        documentReferences.forEach(documentReference -> {

            ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = documentReference.get();

            try {
                Booking booking = documentSnapshotApiFuture.get().toObject(Booking.class);
                bookings.add(booking);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        return bookings;
    }

    public Boolean updateBooking(String id, Booking updatedBooking) throws InterruptedException, ExecutionException {

        Firestore firestore = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> collectionApiFuture = firestore
                .collection(COLLECTION_BOOKING)
                .document(id)
                .set(updatedBooking);

        collectionApiFuture.get();

        return true;
    }

    public Boolean deleteBooking(String id) {

        Firestore firestore = FirestoreClient.getFirestore();

        firestore.collection(COLLECTION_BOOKING).document(id).delete();

        return true;
    }
}