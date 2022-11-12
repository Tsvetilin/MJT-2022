package bg.sofia.uni.fmi.mjt.flightscanner;

import bg.sofia.uni.fmi.mjt.flightscanner.airport.Airport;
import bg.sofia.uni.fmi.mjt.flightscanner.comparators.OrderByDestinationComparator;
import bg.sofia.uni.fmi.mjt.flightscanner.comparators.OrderByFreeSeatsComparator;
import bg.sofia.uni.fmi.mjt.flightscanner.flight.Flight;

import java.util.*;

public class FlightScanner implements FlightScannerAPI {

    private Collection<Flight> flights;

    public FlightScanner() {
        flights = new HashSet<>();
    }

    @Override
    public void add(Flight flight) {
        if (flight == null) {
            throw new IllegalArgumentException("Invalid flight");
        }

        this.flights.add(flight);
    }

    @Override
    public void addAll(Collection<Flight> flights) {
        for (var flight : flights) {
            if (flight == null) {
                throw new IllegalArgumentException("Invalid flight");
            }
        }

        this.flights.addAll(flights);
    }

    private List<Flight> breadFirstSearch(Airport from, Airport to) {
        Map<Airport, List<Flight>> adjecancy = new HashMap<>();

        for (var flight : flights) {
            adjecancy.putIfAbsent(flight.getFrom(), new ArrayList<>());
            adjecancy.get(flight.getFrom()).add(flight);
        }

        Set<Airport> visited = new HashSet<>();
        Queue<Airport> travel = new LinkedList<>();
        Map<Airport, Flight> traveledTo = new HashMap<>();
        travel.add(from);
        traveledTo.put(from, null);

        while (true) {
            if (travel.isEmpty()) {
                break;
            }

            Airport current = travel.remove();

            if (visited.contains(current)) {
                continue;
            }

            visited.add(travel.peek());

            if (current.equals(to)) {
                break;
            }

            if (!adjecancy.containsKey(current)) {
                continue;
            }

            for (var flight : adjecancy.get(current)) {
                if (!visited.contains(flight.getTo())) {
                    travel.add(flight.getTo());
                    traveledTo.put(flight.getTo(), flight);
                }
            }
        }

        Airport current = to;
        List<Flight> result = new ArrayList<>();

        while (current != from) {
            if (!traveledTo.containsKey(current)) {
                break;
            }

            result.add(traveledTo.get(current));
            current = traveledTo.get(current).getFrom();
        }

        Collections.reverse(result);
        return result;
    }

    @Override
    public List<Flight> searchFlights(Airport from, Airport to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Invalid airport");
        }

        if (from.equals(to)) {
            throw new IllegalArgumentException("Cannot start and end in same airport.");
        }

        return breadFirstSearch(from, to);
    }

    private List<Flight> getFlightsFromSorted(Airport from, Comparator<Flight> comparator) {
        if (from == null) {
            throw new IllegalArgumentException("Invalid airport");
        }

        Collection<Flight> filtered = new TreeSet<>(comparator);
        for (var flight : flights) {
            if (flight.getFrom().equals(from)) {
                filtered.add(flight);
            }
        }

        var result = new ArrayList<Flight>(filtered.size());
        result.addAll(filtered);
        return Collections.unmodifiableList(result);
    }


    @Override
    public List<Flight> getFlightsSortedByFreeSeats(Airport from) {
        return getFlightsFromSorted(from, new OrderByFreeSeatsComparator());
    }

    @Override
    public List<Flight> getFlightsSortedByDestination(Airport from) {
        return getFlightsFromSorted(from, new OrderByDestinationComparator());
    }
}
