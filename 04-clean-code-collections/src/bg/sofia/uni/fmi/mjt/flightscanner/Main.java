package bg.sofia.uni.fmi.mjt.flightscanner;

import bg.sofia.uni.fmi.mjt.flightscanner.airport.Airport;
import bg.sofia.uni.fmi.mjt.flightscanner.flight.Flight;
import bg.sofia.uni.fmi.mjt.flightscanner.flight.RegularFlight;

public class Main {
    static final int SEATS = 20;

    public static void main(String... args) {
        FlightScannerAPI scanner = new FlightScanner();


        Airport first = new Airport("SLV");
        Airport second = new Airport("SMD");
        Airport third = new Airport("ASD");
        Airport forth = new Airport("DSA");

        Flight flight = RegularFlight.of("FL1", first, second, SEATS);
        Flight flight1 = RegularFlight.of("FL2", first, third, SEATS);
        Flight flight2 = RegularFlight.of("FL3", second, forth, SEATS);
        Flight flight3 = RegularFlight.of("FL4", third, forth, SEATS);

        scanner.add(flight);
        scanner.add(flight1);
        scanner.add(flight2);
        scanner.add(flight3);

        System.out.println(scanner.getFlightsSortedByDestination(first).size());
        System.out.println(scanner.getFlightsSortedByFreeSeats(first).size());
        System.out.println(scanner.searchFlights(first, second).size());
    }
}
