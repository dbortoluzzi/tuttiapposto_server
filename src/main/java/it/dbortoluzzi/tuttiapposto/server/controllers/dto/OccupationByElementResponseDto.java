package it.dbortoluzzi.tuttiapposto.server.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OccupationByElementResponseDto {
    @NonNull
    private String elementId;
    @NonNull
    private Long occupation;
    private Double occupationPercent;

    @Override
    public String toString() {
        return "OccupationByElementResponseDto{" +
                "elementId='" + elementId + '\'' +
                ", occupation=" + occupation +
                ", occupationPercent=" + occupationPercent +
                '}';
    }
}