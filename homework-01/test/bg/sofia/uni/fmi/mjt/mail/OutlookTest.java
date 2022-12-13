package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OutlookTest {

    private final static String ACCOUNT_NAME = "testAccount";
    private final static String EMAIL_NAME = "email@fmi.bg";
    private final static String SENDER_ACCOUNT_NAME = "Stoyo";
    private final static String SENDER_EMAIL_NAME = "stoyo@fmi.bg";
    private final static String FOLDER_PATH = "/inbox/test";
    private final static String INBOX_FOLDER_PATH = "/inbox";
    private final static String RULE_DEFINITION =
        "subject-includes: MJT, izpit, 2022" + System.lineSeparator() +
            "subject-or-body-includes: izpit" + System.lineSeparator() +
            "from:" + SENDER_EMAIL_NAME + System.lineSeparator() +
            "recipients-includes: " + EMAIL_NAME + System.lineSeparator();
    private final static String MAIL_METADATA = "sender: " + SENDER_EMAIL_NAME + System.lineSeparator() +
        "subject: Hello, MJT 2022 izpit!" + System.lineSeparator() +
        "recipients: pesho@gmail.com," + EMAIL_NAME + ", gosho@gmail.com, " + System.lineSeparator() +
        "received: 2022-12-08 14:14" + System.lineSeparator();
    private final static String MAIL_CONTENT = "Hello from the test!";

    private final MailClient outlook = new Outlook();

    @Test
    void testAddAccountShouldAddAccount() {
        var result = outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        assertEquals(EMAIL_NAME, result.emailAddress(), "Email does not match.");
        assertEquals(ACCOUNT_NAME, result.name(), "Name does not match.");
    }

    @Test
    void testAddAccountShouldAddAccountIfNotExists() {
        assertDoesNotThrow(() -> outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME));
        assertThrows(AccountAlreadyExistsException.class,
            () -> outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME),
            "Should not add duplicate accounts.");
        assertThrows(AccountAlreadyExistsException.class,
            () -> outlook.addNewAccount(ACCOUNT_NAME, "other"),
            "Should not add duplicate accounts.");
        assertThrows(AccountAlreadyExistsException.class,
            () -> outlook.addNewAccount("other", EMAIL_NAME)
            , "Should not add duplicate accounts.");
    }

    @Test
    void testAddAccountShouldThrowWhitInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
            () -> outlook.addNewAccount(null, EMAIL_NAME),
            "Name cannot be null");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.addNewAccount(ACCOUNT_NAME, ""),
            "Email cannot be empty.");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.addNewAccount("  ", EMAIL_NAME),
            "Name cannot be blank.");
    }

    @Test
    void testCreateFolderShouldCrateFolder() {
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        assertDoesNotThrow(() -> outlook.createFolder(ACCOUNT_NAME, FOLDER_PATH),
            "Should create folder if its path starts from root");
    }

    @Test
    void testCreateFolderShouldThrowWhenCreatingSameFolder() {
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        outlook.createFolder(ACCOUNT_NAME, FOLDER_PATH);
        assertThrows(FolderAlreadyExistsException.class,
            () -> outlook.createFolder(ACCOUNT_NAME, FOLDER_PATH),
            "Cannot create existing folders.");
    }

    @Test
    void testCreateFolderShouldThrowWhenInvalidPath() {
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        assertThrows(InvalidPathException.class,
            () -> outlook.createFolder(ACCOUNT_NAME, "someInvalidPath"),
            "Cannot create folder not starting from the root");
        assertThrows(InvalidPathException.class,
            () -> outlook.createFolder(ACCOUNT_NAME, "/inbox/unexisitngFolder/newFolder"),
            "Cannot create folder with missing mediator path");
    }

    @Test
    void testCreateFolderShouldThrowIfAccountNotFound() {
        assertThrows(AccountNotFoundException.class,
            () -> outlook.createFolder(ACCOUNT_NAME, FOLDER_PATH),
            "Cannot create folder for non-existing account");
    }

    @Test
    void testCreateFolderShouldThrowIfInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
            () -> outlook.createFolder(null, FOLDER_PATH),
            "Cannot create folder for null account.");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.createFolder(ACCOUNT_NAME, ""),
            "Cannot create folder with blank name.");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.createFolder("  ", FOLDER_PATH),
            "Cannot create folder for blank account.");
    }

    @Test
    void testAddRuleShouldThrowIfInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () ->
                outlook.addRule(null, FOLDER_PATH, RULE_DEFINITION, 5),
            "Cannot create rule for null account.");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.addRule(ACCOUNT_NAME, " ", RULE_DEFINITION, 5),
            "Cannot create rule for empty folder path.");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.addRule(ACCOUNT_NAME, FOLDER_PATH, "   ", 5),
            "Cannot create rule for blank definition.");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.addRule(ACCOUNT_NAME, FOLDER_PATH, RULE_DEFINITION, 20),
            "Cannot create rule for exceeding priority.");
    }

    @Test
    void testAddRuleShouldThrowIfInvalidAccount() {
        assertThrows(AccountNotFoundException.class,
            () -> outlook.addRule("some acc", FOLDER_PATH, RULE_DEFINITION, 5),
            "Cannot create rule for non-existing account.");
    }

    @Test
    void testAddRuleShouldThrowIfFolderNotFound() {
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        assertThrows(FolderNotFoundException.class,
            () -> outlook.addRule(ACCOUNT_NAME, "/inbox/nonexsiting", RULE_DEFINITION, 5),
            "Cannot create rule for missing folder redirection.");
    }

    @Test
    void testAddRuleShouldThrowIfRedefinitionOfConditions() {
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        outlook.createFolder(ACCOUNT_NAME, FOLDER_PATH);
        assertThrows(RuleAlreadyDefinedException.class,
            () -> outlook.addRule(
                ACCOUNT_NAME,
                FOLDER_PATH,
                RULE_DEFINITION + System.lineSeparator() + "subject-includes: mjt, izpit, 2022" +
                    System.lineSeparator(),
                5),
            "Cannot create rule with conflicting conditions.");

    }

    @Test
    void testAddRuleShouldAddRule() {
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        outlook.createFolder(ACCOUNT_NAME, FOLDER_PATH);
        assertDoesNotThrow(() -> outlook.addRule(
                ACCOUNT_NAME,
                FOLDER_PATH,
                RULE_DEFINITION,
                5),
            "Should add rule when correct data is passed.");

    }

    @Test
    void testAddRuleShouldNotAddConfrontingRule() {
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        outlook.createFolder(ACCOUNT_NAME, FOLDER_PATH);

        outlook.addRule(
            ACCOUNT_NAME,
            INBOX_FOLDER_PATH,
            RULE_DEFINITION,
            5);

        assertDoesNotThrow(() -> outlook.addRule(
                ACCOUNT_NAME,
                FOLDER_PATH,
                RULE_DEFINITION,
                5),
            "Should bypass adding conflicting rules.");
    }

    @Test
    void testAddRuleShouldAddMultipleRulesAndApplyPriority() {
        outlook.addNewAccount(SENDER_ACCOUNT_NAME, SENDER_EMAIL_NAME);
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        outlook.createFolder(ACCOUNT_NAME, FOLDER_PATH);

        assertDoesNotThrow(() -> outlook.addRule(
                ACCOUNT_NAME,
                INBOX_FOLDER_PATH,
                RULE_DEFINITION,
                5),
            "Should add rule.");

        assertDoesNotThrow(() -> outlook.addRule(
                ACCOUNT_NAME,
                FOLDER_PATH,
                RULE_DEFINITION,
                2),
            "Should add rule.");

        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH).size(),
            "Inbox folder should be empty.");
        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, FOLDER_PATH).size(),
            "Custom folder should be empty.");

        outlook.receiveMail(ACCOUNT_NAME, MAIL_METADATA, MAIL_CONTENT);

        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH).size(),
            "Inbox folder should be empty."
        );
        assertEquals(1,
            outlook.getMailsFromFolder(ACCOUNT_NAME, FOLDER_PATH).size(),
            "Custom folder should not be empty after rule is applied on recieved email.");
    }

    @Test
    void testReceiveMailShouldThrowWithInvalidArguments() {
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);

        assertThrows(IllegalArgumentException.class,
            () -> outlook.receiveMail(null, MAIL_METADATA, MAIL_CONTENT));
        assertThrows(IllegalArgumentException.class,
            () -> outlook.receiveMail(ACCOUNT_NAME, "", MAIL_CONTENT));
        assertThrows(IllegalArgumentException.class,
            () -> outlook.receiveMail(ACCOUNT_NAME, MAIL_METADATA, "  "));
        assertThrows(AccountNotFoundException.class,
            () -> outlook.receiveMail("some acc", MAIL_METADATA, MAIL_CONTENT));
    }

    @Test
    void testReceiveMailShouldReceiveMail() {
        outlook.addNewAccount(SENDER_ACCOUNT_NAME, SENDER_EMAIL_NAME);
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);

        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH).size(),
            "Inbox should be empty when no mail is received."
        );

        outlook.receiveMail(ACCOUNT_NAME, MAIL_METADATA, MAIL_CONTENT);

        var result = new ArrayList<>(outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH));

        assertEquals(1, result.size(), "Inbox should have only single mail received.");
        assertTrue(result.get(0).recipients().contains(EMAIL_NAME), "Email should be correctly delivered.");
        assertEquals(result.get(0).body(), MAIL_CONTENT, "Content should be correct.");
    }


    @Test
    void testReceiveMailShouldReceiveMailAndAddToFolder() {
        outlook.addNewAccount(SENDER_ACCOUNT_NAME, SENDER_EMAIL_NAME);
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        outlook.createFolder(ACCOUNT_NAME, FOLDER_PATH);

        outlook.addRule(
            ACCOUNT_NAME,
            FOLDER_PATH,
            RULE_DEFINITION,
            5);

        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH).size(),
            "Inbox folder should be empty when no email is received.");
        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, FOLDER_PATH).size(),
            "Custom folder should be empty when no email is received.");

        outlook.receiveMail(ACCOUNT_NAME, MAIL_METADATA, MAIL_CONTENT);

        var result = new ArrayList<>(outlook.getMailsFromFolder(ACCOUNT_NAME, FOLDER_PATH));

        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH).size(),
            "Inbox folder should be empty as email is placed in custom folder by rule.");
        assertEquals(1,
            result.size(),
            "Custom folder should contain received email.");
        assertTrue(result.get(0).recipients().contains(EMAIL_NAME), "Recipient should be correct.");
        assertEquals(result.get(0).body(), MAIL_CONTENT, "Content should be stored correctly.");
    }

    @Test
    void testGetMailsFromFolderShouldThrowWhenInvalidArguments() {

        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder(null, FOLDER_PATH));
        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder(ACCOUNT_NAME, ""));
        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder("  ", FOLDER_PATH));

        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);

        assertThrows(FolderNotFoundException.class, () -> outlook.getMailsFromFolder(ACCOUNT_NAME, FOLDER_PATH));
        assertThrows(AccountNotFoundException.class, () -> outlook.getMailsFromFolder("some acc", FOLDER_PATH));

    }

    @Test
    void testSendEmailShouldSendEmail() {
        outlook.addNewAccount(SENDER_ACCOUNT_NAME, SENDER_EMAIL_NAME);
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        outlook.createFolder(ACCOUNT_NAME, FOLDER_PATH);

        outlook.addRule(
            ACCOUNT_NAME,
            FOLDER_PATH,
            RULE_DEFINITION,
            5);

        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH).size(),
            "Inbox folder should be empty.");
        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, FOLDER_PATH).size(),
            "Custom folder should be empty.");
        assertEquals(0,
            outlook.getMailsFromFolder(SENDER_ACCOUNT_NAME, "/sent").size(),
            "Sent folder should be empty.");

        assertDoesNotThrow(() -> outlook.sendMail(SENDER_ACCOUNT_NAME, MAIL_METADATA, MAIL_CONTENT),
            "Email should be send and received successfully.");

        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH).size(),
            "Inbox folder should be empty.");
        assertEquals(1,
            outlook.getMailsFromFolder(ACCOUNT_NAME, FOLDER_PATH).size(),
            "Custom folder should be empty.");
        assertEquals(1,
            outlook.getMailsFromFolder(SENDER_ACCOUNT_NAME, "/sent").size(),
            "Sent folder should contain sent mails.");

    }

    @Test
    void testSendEmailShouldSendEmailAndAddSender() {
        outlook.addNewAccount(SENDER_ACCOUNT_NAME, SENDER_EMAIL_NAME);
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        outlook.createFolder(ACCOUNT_NAME, FOLDER_PATH);

        outlook.addRule(
            ACCOUNT_NAME,
            FOLDER_PATH,
            RULE_DEFINITION,
            5);

        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH).size(),
            "Inbox folder should be empty.");
        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, FOLDER_PATH).size(),
            "Custom folder should be empty.");
        assertEquals(0,
            outlook.getMailsFromFolder(SENDER_ACCOUNT_NAME, "/sent").size(),
            "Sent folder should be empty.");

        assertDoesNotThrow(
            () -> outlook.sendMail(SENDER_ACCOUNT_NAME, MAIL_METADATA.replace("sender: stoyo@fmi.bg", ""),
                MAIL_CONTENT));

        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH).size(),
            "Inbox folder should be empty.");
        assertEquals(1,
            outlook.getMailsFromFolder(ACCOUNT_NAME, FOLDER_PATH).size(),
            "Custom folder should be empty.");
        assertEquals(1,
            outlook.getMailsFromFolder(SENDER_ACCOUNT_NAME, "/sent").size(),
            "Sent folder should contain sent mails.");
    }

    @Test
    void testApplyRuleToInboxEmails() {
        outlook.addNewAccount(SENDER_ACCOUNT_NAME, SENDER_EMAIL_NAME);
        outlook.addNewAccount(ACCOUNT_NAME, EMAIL_NAME);
        outlook.createFolder(ACCOUNT_NAME, FOLDER_PATH);

        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH).size(),
            "Inbox folder should be empty.");
        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, FOLDER_PATH).size(),
            "Custom folder should be empty.");
        assertEquals(0,
            outlook.getMailsFromFolder(SENDER_ACCOUNT_NAME, "/sent").size(),
            "Sent folder should be empty.");

        assertDoesNotThrow(
            () -> outlook.sendMail(
                SENDER_ACCOUNT_NAME,
                MAIL_METADATA.replace("sender: stoyo@fmi.bg", ""),
                MAIL_CONTENT));

        assertEquals(1,
            outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH).size(),
            "Inbox folder should contain received email.");
        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, FOLDER_PATH).size(),
            "Custom folder should be empty.");
        assertEquals(1,
            outlook.getMailsFromFolder(SENDER_ACCOUNT_NAME, "/sent").size(),
            "Sent folder should contain sent mails.");

        outlook.addRule(
            ACCOUNT_NAME,
            FOLDER_PATH,
            RULE_DEFINITION,
            5);

        assertEquals(0,
            outlook.getMailsFromFolder(ACCOUNT_NAME, INBOX_FOLDER_PATH).size(),
            "Inbox folder should be empty after email is added to custom folder with rule.");
        assertEquals(1,
            outlook.getMailsFromFolder(ACCOUNT_NAME, FOLDER_PATH).size(),
            "Custom folder should contain email after rule is applied.");
        assertEquals(1,
            outlook.getMailsFromFolder(SENDER_ACCOUNT_NAME, "/sent").size(),
            "Sent folder should contain sent mails.");
    }
}
