package bg.sofia.uni.fmi.mjt.escaperoom.room;

import bg.sofia.uni.fmi.mjt.escaperoom.rating.Ratable;

public class EscapeRoom implements Ratable {

    private String name;
    private Theme theme;
    private double priceToPlay;
    private double avarageRating = 0;
    private int maxReviewsCount;
    private int currentReviewsCount = 0;
    private Difficulty difficulty;
    private int maxTimeToEscape;
    private Review[] reviews;

    public EscapeRoom(String name, Theme theme, Difficulty difficulty, int maxTimeToEscape, double priceToPlay,
                      int maxReviewsCount) {

        this.name = name;
        this.theme = theme;
        this.difficulty = difficulty;
        this.maxTimeToEscape = maxTimeToEscape;
        this.priceToPlay = priceToPlay;
        this.maxReviewsCount = maxReviewsCount;
        this.reviews = new Review[maxReviewsCount];
    }


    /**
     * Returns the name of the escape room.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the difficulty of the escape room.
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Returns the maximum time to escape the room.
     */
    public int getMaxTimeToEscape() {
        return maxTimeToEscape;
    }

    /**
     * Returns all user reviews stored for this escape room, in the order they have been added.
     */
    public Review[] getReviews() {
        int count = Math.min(currentReviewsCount, maxReviewsCount);
        Review[] result = new Review[count];

        System.arraycopy(reviews, 0, result, 0, count);

        return result;
    }

    /**
     * Adds a user review for this escape room.
     * The platform keeps just the latest up to {@code maxReviewsCount} reviews and in case the capacity is full,
     * a newly added review would overwrite the oldest added one, so the platform contains
     * {@code maxReviewsCount} at maximum, at any given time. Note that, despite older reviews may have been
     * overwritten, the rating of the room averages all submitted review ratings, regardless of whether all reviews
     * themselves are still stored in the platform.
     *
     * @param review the user review to add.
     */
    public void addReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException();
        }

        avarageRating = (avarageRating * currentReviewsCount + review.rating()) / (currentReviewsCount + 1);
        ++currentReviewsCount;
        reviews[currentReviewsCount % maxReviewsCount] = review;
    }


    @Override
    public double getRating() {
        return avarageRating;
    }
}
