package it.dbortoluzzi.tuttiapposto.server.controllers;

import it.dbortoluzzi.tuttiapposto.server.controllers.dto.OccupationByElementResponseDto;
import it.dbortoluzzi.tuttiapposto.server.controllers.dto.OccupationRequestDto;
import it.dbortoluzzi.tuttiapposto.server.controllers.dto.OccupationByDateResponseDto;
import it.dbortoluzzi.tuttiapposto.server.services.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class StatisticsController {

    @Autowired
    StatisticsService statisticsService;

    @PostMapping("/api/statistics/occupationStatsPerHour")
    public ResponseEntity<Object> occupationStatsPerHour(@RequestBody OccupationRequestDto occupationRequestDto) {
        try {
            log.info("Requesting occupationStatsPerHour for {}, {}", occupationRequestDto.getStartDate(), occupationRequestDto.getEndDate());
            List<OccupationByDateResponseDto> occupationsByHour = statisticsService.getOccupationStatsPerHour(
                    occupationRequestDto.getCompanyId(),
                    occupationRequestDto.getStartDate(),
                    occupationRequestDto.getEndDate()
            );
            return new ResponseEntity<>(occupationsByHour, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in occupationStatsPerHour for {}", occupationRequestDto, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/statistics/occupationStatsPerRoom")
    public ResponseEntity<Object> getOccupationStatsPerRoom(@RequestBody OccupationRequestDto occupationRequestDto) {
        try {
            log.info("Requesting getOccupationStatsPerRoom for {}, {}", occupationRequestDto.getStartDate(), occupationRequestDto.getEndDate());
            List<OccupationByElementResponseDto> occupationsByRoom = statisticsService.getOccupationStatsPerRoom(
                    occupationRequestDto.getCompanyId(),
                    occupationRequestDto.getStartDate(),
                    occupationRequestDto.getEndDate()
            );
            return new ResponseEntity<>(occupationsByRoom, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in getOccupationStatsPerRoom for {}", occupationRequestDto, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}