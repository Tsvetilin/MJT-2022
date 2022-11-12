package bg.sofia.uni.fmi.mjt.flightscanner.flight;

import bg.sofia.uni.fmi.mjt.flightscanner.airport.Airport;
import bg.sofia.uni.fmi.mjt.flightscanner.exception.FlightCapacityExceededException;
import bg.sofia.uni.fmi.mjt.flightscanner.exception.InvalidFlightException;
import bg.sofia.uni.fmi.mjt.flightscanner.passenger.Passenger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;


public class RegularFlight implements Flight {
    private String flightId;
    private Airport from;
    private Airport to;
    private int totalCapacity;

    private Collection<Passenger> passengers;


    public static RegularFlight of(String flightId, Airport from, Airport to, int totalCapacity) {


        if (flightId == null || flightId.isEmpty() || flightId.isBlank()) {
            throw new IllegalArgumentException("Invalid flight id");
        }

        if (from == null || to == null) {
            throw new IllegalArgumentException("Invalid airport.");
        }

        if (totalCapacity < 0) {
            throw new IllegalArgumentException("Invalid capacity");
        }

        if (from.equals(to)) {
            throw new InvalidFlightException("Cannot start and end in the same airport.");
        }

        RegularFlight result = new RegularFlight();

        result.flightId = flightId;
        result.from = from;
        result.to = to;
        result.totalCapacity = totalCapacity;
        result.passengers = new HashSet<>(totalCapacity);
        return result;
    }

    @Override
    public Airport getFrom() {
        return from;
    }

    @Override
    public Airport getTo() {
        return to;
    }

    @Override
    public void addPassenger(Passenger passenger) throws FlightCapacityExceededException {
        if (this.passengers.size() >= totalCapacity) {
            throw new FlightCapacityExceededException("Capacity exceeded.");
        }

        this.passengers.add(passenger);
    }

    @Override
    public void addPassengers(Collection<Passenger> passengers) throws FlightCapacityExceededException {
        if (this.passengers.size() + passengers.size() > totalCapacity) {
            throw new FlightCapacityExceededException("Capacity exceeded.");
        }

        this.passengers.addAll(passengers);
    }

    @Override
    public Collection<Passenger> getAllPassengers() {
        return Collections.unmodifiableCollection(passengers);
    }

    @Override
    public int getFreeSeatsCount() {
        return totalCapacity - passengers.size();
    }

    @Override
    public int hashCode() {
        return flightId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RegularFlight && flightId.equals(((RegularFlight) o).flightId);
    }
}
