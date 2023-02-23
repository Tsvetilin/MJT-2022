package bg.sofia.uni.fmi.mjt.bookmarks.contracts;

public record Response(String data, ResponseStatus status) {

    public String getDataMessage() {
        return "[ " + status.name() + " ] " + data.trim() + System.lineSeparator();
    }
}
