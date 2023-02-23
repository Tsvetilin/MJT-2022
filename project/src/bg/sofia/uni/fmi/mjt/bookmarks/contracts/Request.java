package bg.sofia.uni.fmi.mjt.bookmarks.contracts;

public record Request(String query) {

    public String getDataMessage() {
        return query.trim() + System.lineSeparator();
    }
}
