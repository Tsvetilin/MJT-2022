public class TourGuide {

    public static int getBestSightseeingPairScore(int[] places) {
        int bestScore = 0;

        for (int i = 0; i < places.length; i++) {
            for (int j = i + 1; j < places.length; j++) {
                int currentScore = places[i] + places[j] + i - j;
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                }
            }
        }

        return bestScore;
    }
}
