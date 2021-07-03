package it.dbortoluzzi.tuttiapposto.server.controllers;

import it.dbortoluzzi.tuttiapposto.server.controllers.dto.AvailabilityRequest;
import it.dbortoluzzi.tuttiapposto.server.models.Table;
import it.dbortoluzzi.tuttiapposto.server.services.AvailabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class TableController {

    @Autowired
    AvailabilityService availabilityService;

    @PostMapping("/api/tables/available")
    public ResponseEntity<Object> findAvailableTables(@RequestBody AvailabilityRequest availabilityRequest) {
        try {
            List<Table> availableTables = availabilityService.findAvailableTables(
                    availabilityRequest.getCompanyId(),
                    Optional.ofNullable(availabilityRequest.getBuildingId()),
                    Optional.ofNullable(availabilityRequest.getRoomId()),
                    availabilityRequest.getStartDate(),
                    availabilityRequest.getEndDate()
            );
            return new ResponseEntity<>(availableTables, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error searching availability for {}", availabilityRequest);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}