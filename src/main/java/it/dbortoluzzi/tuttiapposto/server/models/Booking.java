package it.dbortoluzzi.tuttiapposto.server.models;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @DocumentId
    private String id;
    private String userUid;
    private String companyUid;

    public Booking(String id, String userUid, String companyUid) {
        this.id = id;
        this.userUid = userUid;
        this.companyUid = companyUid;
    }
}