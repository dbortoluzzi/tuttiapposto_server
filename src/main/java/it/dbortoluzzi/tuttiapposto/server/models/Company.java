package it.dbortoluzzi.tuttiapposto.server.models;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @DocumentId
    private String uID;
    private String vatNumber;
    @NonNull
    private String denomination;
    private Boolean active;
    private Boolean selfGranted;
    private Float maxCapacityPercentage;

}