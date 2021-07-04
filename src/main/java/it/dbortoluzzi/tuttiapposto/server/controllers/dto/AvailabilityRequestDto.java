package it.dbortoluzzi.tuttiapposto.server.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class AvailabilityRequestDto {
    @NonNull
    private String companyId;
    private String buildingId;
    private String roomId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    @Override
    public String toString() {
        return "AvailabilityRequest{" +
                "companyId='" + companyId + '\'' +
                ", buildingId='" + buildingId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}