package it.dbortoluzzi.tuttiapposto.server.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Building {

    @DocumentId
    @JsonProperty("uID")
    private String uID;
    @NonNull
    private String companyId;
    private Boolean active;
    @NonNull
    private String name;
    private String address;
}