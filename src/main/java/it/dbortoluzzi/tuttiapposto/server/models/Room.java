package it.dbortoluzzi.tuttiapposto.server.models;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @DocumentId
    private String id;
    @NonNull
    private String companyId;
    @NonNull
    private String buildingId;
    private Boolean active;
    @NonNull
    private String name;
    private String description;
    private Integer maxCapacity;
}