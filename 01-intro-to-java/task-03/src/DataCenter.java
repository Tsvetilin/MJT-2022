public class DataCenter {

    public static int getCommunicatingServersCount(int[][] map) {
        int count = 0;
        int[][] array = map.clone();
        int n = array.length;
        int m = array[0].length;

        for (int i = 0; i < n; i++) {
            int current = 0;
            for (int j = 0; j < m; j++) {
                if (array[i][j] > 0) {
                    current++;
                }
            }

            if (current < 2) {
                continue;
            }

            count += current;
            for (int j = 0; j < m; j++) {
                if (array[i][j] == 1) {
                    array[i][j] = 2;
                }
            }
        }

        for (int j = 0; j < m; j++) {
            int current = 0;
            for (int i = 0; i < n; i++) {
                if (array[i][j] > 0) {
                    current++;
                }
            }

            if (current < 2) {
                continue;
            }

            for (int i = 0; i < n; i++) {
                if (array[i][j] == 1) {
                    ++count;
                }
            }
        }

        return count;
    }
}
