package bg.sofia.uni.fmi.mjt.netflix;


import java.util.List;

public record Content(String id, String title, ContentType type, String description, int releaseYear, int runtime,
                      List<String> genres, int seasons, String imdbId, double imdbScore, double imdbVotes) {
}
