public class PrefixExtractor {
 
    public static String getLongestCommonPrefix(String[] words) {
 
        if(words==null || words.length==0){
            return "";
        }
 
        if (words.length == 1) {
            return words[0];
        }
 
        StringBuilder prefix = new StringBuilder();
 
        for (int letterIndex = 0; letterIndex < words[0].length(); ++letterIndex) {
            boolean isPrefix = true;
 
            for (int wordIndex = 0; wordIndex < words.length; ++wordIndex) {
                if (words[wordIndex].length() <= letterIndex
                        || words[wordIndex].toLowerCase().charAt(letterIndex) != words[0].toLowerCase().charAt(letterIndex)) {
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
}