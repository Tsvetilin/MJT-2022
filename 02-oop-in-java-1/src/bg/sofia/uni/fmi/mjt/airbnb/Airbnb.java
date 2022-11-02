package bg.sofia.uni.fmi.mjt.airbnb;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;
import bg.sofia.uni.fmi.mjt.airbnb.filter.Criterion;

public class Airbnb implements AirbnbAPI {

    private Bookable[] accommodations;

   public Airbnb(Bookable[] accommodations) {
        this.accommodations = accommodations;
    }

    @Override
    public Bookable findAccommodationById(String id) {
        if (id == null || id.isBlank() || id.isEmpty()) {
            return null;
        }

        for (int i = 0; i < accommodations.length; i++) {
            if (accommodations[i].getId().toLowerCase().equals(id.toLowerCase())) {
                return accommodations[i];
            }
        }

        return null;
    }

    @Override
    public double estimateTotalRevenue() {
        double result = 0;

        for (int i = 0; i < accommodations.length; i++) {
            if (accommodations[i].isBooked()) {
                result += accommodations[i].getTotalPriceOfStay();
            }
        }

        return result;
    }

    @Override
    public long countBookings() {

        int result = 0;
        for (int i = 0; i < accommodations.length; i++) {
            if (accommodations[i].isBooked()) {
                ++result;
            }
        }

        return result;

    }

    @Override
    public Bookable[] filterAccommodations(Criterion... criteria) {
        Bookable[] bookables = new Bookable[accommodations.length];
        int index = 0;

        for (int i = 0; i < accommodations.length; i++) {
            boolean filtered = true;
            for (var crit : criteria) {
                filtered &= crit.check(accommodations[i]);
            }
            if(filtered){
                bookables[index++] = accommodations[i];
            }
        }

        Bookable[] result = new Bookable[index];

        for (int i = 0; i < index; i++) {
            result[i] = bookables[i];
        }

        return result;
    }
}
