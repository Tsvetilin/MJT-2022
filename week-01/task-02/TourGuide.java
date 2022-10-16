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

    public static void main(String[] args) {
        //System.out.println(getBestSightseeingPairScore(new int[]{1, 2}));
        System.out.println(getBestSightseeingPairScore(new int[] { 8, 1, 5, 2, 6 }));
    }
}
