package it.dbortoluzzi.tuttiapposto.server.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @DocumentId
    @JsonProperty("uID")
    private String uID;
    @NonNull
    private String companyId;
    @NonNull
    private String buildingId;
    private Boolean active;
    @NonNull
    private String name;
    private String description;
}