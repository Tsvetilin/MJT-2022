package bg.sofia.uni.fmi.mjt.flightscanner.comparators;

import bg.sofia.uni.fmi.mjt.flightscanner.flight.Flight;

import java.util.Comparator;

public class OrderByDestinationComparator implements Comparator<Flight> {
    @Override
    public int compare(Flight o1, Flight o2) {
        return o1.getTo().id().compareTo(o2.getTo().id());
    }
}
