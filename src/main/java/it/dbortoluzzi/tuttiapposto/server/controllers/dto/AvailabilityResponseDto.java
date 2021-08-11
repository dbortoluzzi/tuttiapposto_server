package it.dbortoluzzi.tuttiapposto.server.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.dbortoluzzi.tuttiapposto.server.models.Company;
import it.dbortoluzzi.tuttiapposto.server.models.Table;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponseDto {

    @NonNull
    private Table table;
    @NonNull
    private Integer availability;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    private Boolean reported;
    private Boolean alreadyReportedByUser;

    public AvailabilityResponseDto(@NonNull Table table, @NonNull Integer availability, Date startDate, Date endDate) {
        this.table = table;
        this.availability = availability;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reported = false;
        this.alreadyReportedByUser = false;
    }

    @Override
    public String toString() {
        return "AvailabilityResponseDto{" +
                "table=" + table +
                ", availability=" + availability +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", reported=" + reported +
                ", alreadyReportedByUser=" + alreadyReportedByUser +
                '}';
    }
}