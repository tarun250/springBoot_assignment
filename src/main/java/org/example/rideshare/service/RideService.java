package org.example.rideshare.service;

import org.example.rideshare.dto.CreateRideRequest;
import org.example.rideshare.dto.RideResponse;
import org.example.rideshare.exception.BadRequestException;
import org.example.rideshare.exception.NotFoundException;
import org.example.rideshare.model.Ride;
import org.example.rideshare.model.User;
import org.example.rideshare.repository.RideRepository;
import org.example.rideshare.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public RideService(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    private User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private RideResponse toResponse(Ride r) {
        return new RideResponse(
                r.getId(),
                r.getUserId(),
                r.getDriverId(),
                r.getPickupLocation(),
                r.getDropLocation(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }

    public RideResponse createRide(CreateRideRequest request) {
        User user = getLoggedInUser();
        if (!"ROLE_USER".equals(user.getRole())) {
            throw new BadRequestException("Only ROLE_USER can request ride");
        }

        Ride ride = new Ride();
        ride.setUserId(user.getId());
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDropLocation(request.getDropLocation());
        ride.setStatus("REQUESTED");
        ride.setCreatedAt(new Date());

        rideRepository.save(ride);

        return toResponse(ride);
    }

    public List<RideResponse> getPendingRidesForDriver() {
        User driver = getLoggedInUser();
        if (!"ROLE_DRIVER".equals(driver.getRole())) {
            throw new BadRequestException("Only ROLE_DRIVER can view pending rides");
        }

        return rideRepository.findByStatus("REQUESTED")
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RideResponse acceptRide(String rideId) {
        User driver = getLoggedInUser();
        if (!"ROLE_DRIVER".equals(driver.getRole())) {
            throw new BadRequestException("Only ROLE_DRIVER can accept rides");
        }

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!"REQUESTED".equals(ride.getStatus())) {
            throw new BadRequestException("Ride must be REQUESTED to accept");
        }

        ride.setDriverId(driver.getId());
        ride.setStatus("ACCEPTED");

        rideRepository.save(ride);

        return toResponse(ride);
    }

    public List<RideResponse> getMyRides() {
        User user = getLoggedInUser();
        return rideRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RideResponse completeRide(String rideId) {
        getLoggedInUser(); // just to ensure authenticated

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!"ACCEPTED".equals(ride.getStatus())) {
            throw new BadRequestException("Ride must be ACCEPTED to complete");
        }

        ride.setStatus("COMPLETED");
        rideRepository.save(ride);

        return toResponse(ride);
    }
}
