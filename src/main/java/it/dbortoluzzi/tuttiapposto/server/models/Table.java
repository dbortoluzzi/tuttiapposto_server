package it.dbortoluzzi.tuttiapposto.server.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Table {

    @DocumentId
    @JsonProperty("uID")
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

    public static Integer computeRealCapacity(Table table, Company company) {
        return (int)Math.floor(table.getMaxCapacity() * company.getMaxCapacityPercentage());
    }

    @Override
    public String toString() {
        return "Table{" +
                "uID='" + uID + '\'' +
                ", companyId='" + companyId + '\'' +
                ", buildingId='" + buildingId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", active=" + active +
                ", name='" + name + '\'' +
                ", maxCapacity=" + maxCapacity +
                '}';
    }
}