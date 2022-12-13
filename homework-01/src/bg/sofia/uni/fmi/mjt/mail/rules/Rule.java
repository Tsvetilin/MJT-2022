package bg.sofia.uni.fmi.mjt.mail.rules;

import bg.sofia.uni.fmi.mjt.mail.Mail;
import bg.sofia.uni.fmi.mjt.mail.rules.conditions.Condition;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public record Rule(List<Condition> conditions, int priority, String folderPath) {

    public boolean match(Mail mail) {
        for (var condition : conditions) {
            if (!condition.match(mail)) {
                return false;
            }
        }

        return true;
    }

    // Two rules are equal if the conditions and priority are the same
    // NB: they may have different folders

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return priority == rule.priority &&
            new HashSet<>(conditions).containsAll(rule.conditions) &&
            new HashSet<>(rule.conditions).containsAll(conditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditions, priority);
    }
}
