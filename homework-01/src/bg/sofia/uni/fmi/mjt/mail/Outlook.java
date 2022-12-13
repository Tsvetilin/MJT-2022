package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import bg.sofia.uni.fmi.mjt.mail.rules.Rule;
import bg.sofia.uni.fmi.mjt.mail.rules.RulePriorityComparator;
import bg.sofia.uni.fmi.mjt.mail.rules.conditions.Condition;
import bg.sofia.uni.fmi.mjt.mail.rules.conditions.FromCondition;
import bg.sofia.uni.fmi.mjt.mail.rules.conditions.RecipientsIncludeCondition;
import bg.sofia.uni.fmi.mjt.mail.rules.conditions.SubjectIncludesCondition;
import bg.sofia.uni.fmi.mjt.mail.rules.conditions.SubjectOrBodyIncludesCondition;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Outlook implements MailClient {

    private static final int MAX_PRIORITY = 10;
    private static final String INBOX_PATH = "/inbox";
    private static final String SENT_PATH = "/sent";

    private Map<String, Map<String, List<Mail>>> mails;
    private Map<String, Set<Rule>> rules;
    private Map<String, Account> accounts;

    public Outlook() {
        mails = new HashMap<>();
        accounts = new HashMap<>();
        rules = new HashMap<>();
    }

    @Override
    public Account addNewAccount(String accountName, String email) {
        throwIfInvalidString(accountName, email);

        for (var e : accounts.values()) {
            if (e.emailAddress().equals(email) || e.name().equals(accountName)) {
                throw new AccountAlreadyExistsException("Cannot add account with existing name.");
            }
        }

        var account = new Account(email, accountName);
        accounts.put(accountName, account);
        mails.put(accountName, new HashMap<>());
        mails.get(accountName).put(INBOX_PATH, new ArrayList<>());
        mails.get(accountName).put(SENT_PATH, new ArrayList<>());
        rules.put(accountName, new TreeSet<>(new RulePriorityComparator()));

        return account;
    }

    @Override
    public void createFolder(String accountName, String path) {
        throwIfInvalidString(accountName, path);

        throwIfAccountNotFound(accountName);

        if (mails.get(accountName).containsKey(path)) {
            throw new FolderAlreadyExistsException("Folder already exists.");
        }

        if (!path.startsWith(INBOX_PATH + "/")) {
            throw new InvalidPathException("Folder path should start from root.");
        }

        if (!mails.get(accountName).containsKey(path.substring(0, path.lastIndexOf("/")))) {
            throw new InvalidPathException("Folder path contains non-existing folders.");
        }

        mails.get(accountName).put(path, new ArrayList<>());
    }

    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        throwIfInvalidString(accountName, folderPath, ruleDefinition);

        if (priority < 1 || priority > MAX_PRIORITY) {
            throw new IllegalArgumentException("Invalid priority range.");
        }

        throwIfAccountNotFound(accountName);

        if (!mails.get(accountName).containsKey(folderPath)) {
            throw new FolderNotFoundException("Folder not found.");
        }

        List<Condition> conditions = new ArrayList<>();
        Set<String> conditionsDefined = new HashSet<>();
        String[] lines = ruleDefinition.trim().split(System.lineSeparator());
        for (String line : lines) {
            var condition = line.split(":");

            if (condition.length < 2) {
                continue;
            }

            if (conditionsDefined.contains(condition[0].trim())) {
                throw new RuleAlreadyDefinedException("Cannot add duplicate conditions.");
            }

            conditionsDefined.add(condition[0].trim());

            conditions.add(parseCondition(condition[0].trim(), line.substring(line.indexOf(":") + 1).trim()));
        }

        var rule = new Rule(conditions, priority, folderPath);
        for (var r : rules.get(accountName)) {
            if (r.equals(rule)) {
                // Undefined in task statement behaviour -> not applying the rule
                return;
            }
        }

        rules.get(accountName).add(rule);
        applyInboxRule(accountName, rule);
    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {
        throwIfInvalidString(accountName, mailMetadata, mailContent);
        throwIfAccountNotFound(accountName);

        var subject = extractSubject(mailMetadata);
        var recipients = extractRecipients(mailMetadata);
        var sender = extractSender(mailMetadata);
        var time = extractReceivedTime(mailMetadata);

        Account senderAccount = null;
        for (var account : accounts.values()) {
            if (account.emailAddress().equals(sender)) {
                senderAccount = account;
                break;
            }
        }

        if (senderAccount == null) {
            throw new AccountNotFoundException("No such account exists.");
        }

        var mail = new Mail(senderAccount, recipients, subject, mailContent, time);

        applyRules(accountName, mail);
    }


    @Override
    public Collection<Mail> getMailsFromFolder(String account, String folderPath) {
        throwIfInvalidString(account, folderPath);
        throwIfAccountNotFound(account);

        if (!mails.get(account).containsKey(folderPath)) {
            throw new FolderNotFoundException("Cannot find such folder.");
        }

        return mails.get(account).get(folderPath);
    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {
        throwIfInvalidString(accountName, mailMetadata, mailContent);
        throwIfAccountNotFound(accountName);

        Set<String> recipients = extractRecipients(mailMetadata);
        String subject = extractSubject(mailMetadata);
        var sentTime = extractReceivedTime(mailMetadata);
        mailMetadata = includeSender(mailMetadata, accounts.get(accountName).emailAddress());
        var mail = new Mail(accounts.get(accountName), recipients, subject, mailContent, sentTime);

        addToFolder(accountName, SENT_PATH, mail);

        for (var recipient : recipients) {
            for (var account : accounts.values()) {
                if (account.emailAddress().equals(recipient)) {
                    receiveMail(account.name(), mailMetadata, mailContent);
                }
            }
        }
    }

    private void throwIfInvalidString(String... strings) {
        for (var str : strings) {
            if (str == null || str.isEmpty() || str.isBlank()) {
                throw new IllegalArgumentException("Invalid string parameter.");
            }
        }
    }

    private void throwIfAccountNotFound(String account) {
        if (!accounts.containsKey(account)) {
            throw new AccountNotFoundException("Account does not exist.");
        }
    }

    private void addToFolder(String accountName, String folder, Mail mail) {
        mails.get(accountName).get(folder).add(mail);
    }

    private String includeSender(String metadata, String sender) {
        StringBuilder result = new StringBuilder();
        var lines = metadata.split(System.lineSeparator());
        boolean senderAdded = false;
        for (var line : lines) {
            var lineSplit = line.split(":");
            if (lineSplit[0].trim().equals("sender")) {
                result.append("sender: ").append(sender).append(System.lineSeparator());
                senderAdded = true;
            } else {
                result.append(line).append(System.lineSeparator());
            }
        }

        if (!senderAdded) {
            result.append("sender: ").append(sender).append(System.lineSeparator());
        }

        return result.toString();
    }

    private Condition parseCondition(String name, String params) {
        var splitParams = params.split(",");
        Set<String> parsedParams = new HashSet<>();
        for (var param : splitParams) {
            parsedParams.add(param.trim());
        }

        return switch (name) {
            case "subject-includes" -> SubjectIncludesCondition.of(parsedParams);
            case "subject-or-body-includes" -> SubjectOrBodyIncludesCondition.of(parsedParams);
            case "recipients-includes" -> RecipientsIncludeCondition.of(parsedParams);
            case "from" -> FromCondition.of(params.trim());
            default -> throw new IllegalArgumentException("Invalid condition.");
        };
    }

    private void applyRules(String account, Mail mail) {
        var rulesForAccount = rules.get(account);

        for (var rule : rulesForAccount) {
            if (rule.match(mail)) {
                addToFolder(account, rule.folderPath(), mail);
                return;
            }
        }

        addToFolder(account, INBOX_PATH, mail);
    }

    private void applyInboxRule(String account, Rule rule) {
        var mailsInbox = Set.copyOf(mails.get(account).get(INBOX_PATH));

        for (var mail : mailsInbox) {
            if (rule.match(mail)) {
                addToFolder(account, rule.folderPath(), mail);
                mails.get(account).get(INBOX_PATH).remove(mail);
            }
        }
    }

    String getField(String data, String field) {
        var lines = data.split(System.lineSeparator());
        for (var line : lines) {
            var lineSplit = line.split(":");
            if (lineSplit[0].trim().equals(field)) {
                return line.substring(line.indexOf(":") + 1).trim();
            }
        }

        throw new IllegalArgumentException("Field not found in metadata.");
    }

    private Set<String> extractRecipients(String metadata) {
        Set<String> recipients = new HashSet<>();
        var splitRecipients = getField(metadata, "recipients").split(",");
        for (var r : splitRecipients) {
            recipients.add(r.trim());
        }
        return recipients;
    }

    private String extractSubject(String metadata) {
        return getField(metadata, "subject");
    }

    private String extractSender(String metadata) {
        return getField(metadata, "sender");
    }

    private LocalDateTime extractReceivedTime(String metadata) {
        return LocalDateTime.parse(getField(metadata, "received"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
