package bg.sofia.uni.fmi.mjt.mail.rules.conditions;

import bg.sofia.uni.fmi.mjt.mail.Mail;

import java.util.Objects;

public class FromCondition implements Condition {

    private String fromEmail;

    public static FromCondition of(String email) {
        var result = new FromCondition();
        result.fromEmail = email;
        return result;
    }

    @Override
    public boolean match(Mail mail) {
        return mail.sender().emailAddress().equals(fromEmail);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FromCondition that = (FromCondition) o;
        return Objects.equals(fromEmail, that.fromEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromEmail);
    }
}
