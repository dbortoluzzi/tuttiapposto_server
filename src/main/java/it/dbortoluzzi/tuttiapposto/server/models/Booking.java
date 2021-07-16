package it.dbortoluzzi.tuttiapposto.server.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

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
    @NonNull
    private Date startDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @NonNull
    private Date endDate;
    private List<Date> days;

    public Booking(String uID, @NonNull String userId, @NonNull String companyId, @NonNull String buildingId, @NonNull String roomId, @NonNull String tableId, @NonNull Date startDate, @NonNull Date endDate) {
        this.uID = uID;
        this.userId = userId;
        this.companyId = companyId;
        this.buildingId = buildingId;
        this.roomId = roomId;
        this.tableId = tableId;
        this.startDate = startDate;
        this.endDate = endDate;
        rebuildDates();
    }

    public void rebuildDates() {
        List<Date> daysFound = new ArrayList<>();
        Date checkDate = DateUtils.truncate(startDate, Calendar.DATE);
        while (checkDate.before(endDate)) {
            daysFound.add(checkDate);
            checkDate = DateUtils.addDays(checkDate, 1);
        }
        this.days = daysFound;
    }

    @Override
    public String toString() {
        return "Booking{" +
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