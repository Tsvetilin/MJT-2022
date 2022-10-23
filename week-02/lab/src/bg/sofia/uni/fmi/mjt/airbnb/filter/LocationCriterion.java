package bg.sofia.uni.fmi.mjt.airbnb.filter;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;
import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

public class LocationCriterion implements Criterion {

    private Location current;
    private double maxDistance;

    public LocationCriterion(Location currentLocation, double maxDistance) {
        this.current = currentLocation;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean check(Bookable bookable) {
        if (bookable == null) {
            return false;
        }

        return bookable.getLocation().distanceTo(current) <= maxDistance;
    }
}
