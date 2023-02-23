package bg.sofia.uni.fmi.mjt.bookmarks.server.logging;

public enum Severity {
    NONE(0),
    INFO(1),
    WARN(2),
    ERROR(3);

    private final int value;

    Severity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
