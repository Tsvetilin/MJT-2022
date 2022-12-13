package bg.sofia.uni.fmi.mjt.mail.rules.conditions;

import bg.sofia.uni.fmi.mjt.mail.Mail;

import java.util.Objects;
import java.util.Set;

public class SubjectOrBodyIncludesCondition implements Condition {
    private Set<String> expressions;

    public static SubjectOrBodyIncludesCondition of(Set<String> expr) {
        SubjectOrBodyIncludesCondition res = new SubjectOrBodyIncludesCondition();
        res.expressions = expr;
        return res;
    }

    @Override
    public boolean match(Mail mail) {
        String search = mail.subject() + " " + mail.body();

        for (var expr : expressions) {
            if (!search.contains(expr)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectOrBodyIncludesCondition that = (SubjectOrBodyIncludesCondition) o;
        return expressions.containsAll(that.expressions) && that.expressions.containsAll(expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressions);
    }
}
