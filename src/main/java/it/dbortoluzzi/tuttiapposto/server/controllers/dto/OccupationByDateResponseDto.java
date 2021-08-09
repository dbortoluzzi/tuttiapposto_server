package it.dbortoluzzi.tuttiapposto.server.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OccupationByDateResponseDto {
    @NonNull
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date date;
    @NonNull
    private Long occupation;

    @Override
    public String toString() {
        return "OccupationResponseDto{" +
                "date=" + date +
                ", occupation=" + occupation +
                '}';
    }
}