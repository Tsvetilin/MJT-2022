package bg.sofia.uni.fmi.mjt.mail.rules.conditions;

import bg.sofia.uni.fmi.mjt.mail.Mail;

public interface Condition {

    boolean match(Mail mail);
}
