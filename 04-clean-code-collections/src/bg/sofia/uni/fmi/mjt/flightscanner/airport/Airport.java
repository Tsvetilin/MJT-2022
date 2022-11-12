package bg.sofia.uni.fmi.mjt.flightscanner.airport;

import java.util.Objects;

public record Airport(String id) {

    public Airport {
        if (id == null || id.isBlank() || id.isEmpty()) {
            throw new IllegalArgumentException("Invalid id");
        }

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Airport && Objects.equals(((Airport) other).id, id);
    }
}