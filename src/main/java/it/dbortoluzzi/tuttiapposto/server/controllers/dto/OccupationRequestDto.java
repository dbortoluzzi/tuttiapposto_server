package it.dbortoluzzi.tuttiapposto.server.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OccupationRequestDto {
    @NonNull
    private String companyId;
    @NonNull
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    @NonNull
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    @Override
    public String toString() {
        return "OccupationByHourRequestDto{" +
                "companyId='" + companyId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}