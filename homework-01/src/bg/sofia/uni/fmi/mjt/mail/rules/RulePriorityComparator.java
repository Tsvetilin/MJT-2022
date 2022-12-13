package bg.sofia.uni.fmi.mjt.mail.rules;

import java.util.Comparator;

public class RulePriorityComparator implements Comparator<Rule> {

    @Override
    public int compare(Rule o1, Rule o2) {
        return Integer.compare(o1.priority(), o2.priority());
    }
}
