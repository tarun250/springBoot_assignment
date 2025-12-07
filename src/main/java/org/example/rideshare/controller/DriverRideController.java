package org.example.rideshare.controller;

import org.example.rideshare.dto.RideResponse;
import org.example.rideshare.service.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/driver")
public class DriverRideController {

    private final RideService rideService;

    public DriverRideController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping("/rides/requests")
    public ResponseEntity<List<RideResponse>> getPendingRides() {
        return ResponseEntity.ok(rideService.getPendingRidesForDriver());
    }

    @PostMapping("/rides/{rideId}/accept")
    public ResponseEntity<RideResponse> acceptRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.acceptRide(rideId));
    }
}
