package bg.sofia.uni.fmi.mjt.mail.rules.conditions;

import bg.sofia.uni.fmi.mjt.mail.Mail;

import java.util.Objects;
import java.util.Set;

public class SubjectIncludesCondition implements Condition {

    private Set<String> expressions;

    public static SubjectIncludesCondition of(Set<String> expr) {
        SubjectIncludesCondition res = new SubjectIncludesCondition();
        res.expressions = expr;
        return res;
    }

    @Override
    public boolean match(Mail mail) {
        for (var expr : expressions) {
            if (!mail.subject().contains(expr)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectIncludesCondition that = (SubjectIncludesCondition) o;
        return expressions.containsAll(that.expressions) && that.expressions.containsAll(expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressions);
    }
}
