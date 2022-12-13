package bg.sofia.uni.fmi.mjt.mail.rules.conditions;

import bg.sofia.uni.fmi.mjt.mail.Mail;

import java.util.Objects;
import java.util.Set;

public class RecipientsIncludeCondition implements Condition {

    Set<String> recipients;

    public static RecipientsIncludeCondition of(Set<String> recipients) {
        RecipientsIncludeCondition res = new RecipientsIncludeCondition();
        res.recipients = recipients;
        return res;
    }

    @Override
    public boolean match(Mail mail) {
        for (var r : mail.recipients()) {
            if (recipients.contains(r)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipientsIncludeCondition that = (RecipientsIncludeCondition) o;
        return recipients.containsAll(that.recipients) && that.recipients.containsAll(recipients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipients);
    }
}
