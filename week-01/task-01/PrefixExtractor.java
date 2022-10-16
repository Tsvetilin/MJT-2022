
class PrefixExtractor {

    public static String getLongestCommonPrefix(String[] words) {

        if (words.length == 1) {
            return words[0];
        }

        StringBuilder prefix = new StringBuilder();

        for (int letterIndex = 0; letterIndex < words[0].length(); ++letterIndex) {
            boolean isPrefix = true;

            for (int wordIndex = 0; wordIndex < words.length; ++wordIndex) {
                if (words[wordIndex].length() <= letterIndex
                        || words[wordIndex].charAt(letterIndex) != words[0].charAt(letterIndex)) {
                    isPrefix = false;
                    break;
                }
            }

            if (!isPrefix) {
                break;
            }

            prefix.append(words[0].charAt(letterIndex));
        }

        return prefix.toString();
    }

    public static void main(String[] args) {
        // {"cat"};
        // {"dog", "racecar", "car"};
        String[] words = { "flower", "flow", "flight" };
        System.out.println(getLongestCommonPrefix(words));

    }
}
