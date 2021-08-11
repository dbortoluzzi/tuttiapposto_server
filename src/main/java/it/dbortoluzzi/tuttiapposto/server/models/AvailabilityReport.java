package it.dbortoluzzi.tuttiapposto.server.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityReport {
    @DocumentId
    @JsonProperty("uID")
    private String uID;
    @NonNull
    private String userId;
    @NonNull
    private String companyId;
    @NonNull
    private String buildingId;
    @NonNull
    private String roomId;
    @NonNull
    private String tableId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    @Override
    public String toString() {
        return "AvailabilityReport{" +
                "uID='" + uID + '\'' +
                ", userId='" + userId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", buildingId='" + buildingId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}