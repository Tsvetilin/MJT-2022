package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public abstract class Accommodation implements Bookable {

    private String id;
    private Location location;
    private double pricePerNight;
    private boolean isBooked;

    private LocalDateTime checkOut;
    private LocalDateTime checkIn;

    public Accommodation(Location location, double pricePerNight, String id) {
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isBooked() {
        return isBooked;
    }

    @Override
    public boolean book(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (isBooked) {
            return false;
        }

        if (checkIn == null || checkOut == null) {
            return false;
        }

        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut) || LocalDateTime.now().isAfter(checkIn)) {
            return false;
        }

        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.isBooked = true;

        return true;
    }

    @Override
    public double getTotalPriceOfStay() {
        return (isBooked ? checkIn.until(checkOut, ChronoUnit.DAYS) * this.pricePerNight : 0);
    }

    @Override
    public double getPricePerNight() {
        return this.pricePerNight;
    }
}
