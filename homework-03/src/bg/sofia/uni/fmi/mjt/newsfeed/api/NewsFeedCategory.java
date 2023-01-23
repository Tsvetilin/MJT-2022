package bg.sofia.uni.fmi.mjt.newsfeed.api;

public enum NewsFeedCategory {
    DEFAULT(""),
    BUSINESS("business"),
    ENTERTAINMENT("entertainment"),
    GENERAL("general"),
    HEALTH("health"),
    SCIENCE("science"),
    SPORTS("sports"),
    TECHNOLOGY("technology");

    final String name;

    NewsFeedCategory(String name) {
        this.name = name;
    }

    public String category() {
        return name;
    }

}
