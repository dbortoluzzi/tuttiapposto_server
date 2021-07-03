package it.dbortoluzzi.tuttiapposto.server.models;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Table {

    @DocumentId
    private String uID;
    @NonNull
    private String companyId;
    @NonNull
    private String buildingId;
    @NonNull
    private String roomId;
    private Boolean active;
    @NonNull
    private String name;
    @NonNull
    private Integer maxCapacity;
}