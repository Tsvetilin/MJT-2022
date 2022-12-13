package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;

import java.util.Collection;

public interface MailClient {

    /**
     * Creates a new account in the MailClient
     *
     * @param accountName short name of the account
     * @param email       email of the account
     * @return the created Account
     * @throws IllegalArgumentException      if any of the string parameters is null, empty or blank
     * @throws AccountAlreadyExistsException if account with the same name is already present in the client
     */
    Account addNewAccount(String accountName, String email);

    /**
     * @param accountName name of the account for which the folder is created
     * @param path        full path to the folder. The root folder and the path separator character
     *                    is forward slash ('/')
     * @throws IllegalArgumentException     if any of the string parameters is null, empty or blank
     * @throws AccountNotFoundException     if the account is not present
     * @throws InvalidPathException         if the folder path does not start from the root folder
     *                                      of received mails, or if some intermediate folders do not exist
     * @throws FolderAlreadyExistsException if folder with the same absolute path is already present
     *                                      for the provided account
     */
    void createFolder(String accountName, String path);

    /**
     * Creates a new Rule for the current mail client.
     * A Rule is defined via a string called Rule Definition. Each Rule Definition contains one or more Rule Conditions.
     * <p>
     * The following Rule Definition is the valid format for rules:
     * subject-includes: <list-of-keywords>
     * subject-or-body-includes: <list-of-keywords>
     * recipients-includes: <list-of-recipient-emails>
     * from: <sender-email>
     * <p>
     * The order is not determined, and the list might not be full. Example:
     * subject-includes: mjt, izpit, 2022
     * subject-or-body-includes: izpit
     * from: stoyo@fmi.bg
     * <p>
     * For subject-includes and subject-or-body-includes rule conditions, if more than one keywords is specified,
     * all must
     * be contained for the rule to match, i.e. it is a conjunction condition. For recipients-includes,
     * it's enough for one listed recipient to match (disjunction condition). For from, it should be exact match.
     *
     * @param accountName    name of the account for which the rule is applied
     * @param folderPath     full path of the destination folder
     * @param ruleDefinition string definition of the rule
     * @param priority       priority of the rule - [1,10], 1 = highest priority
     * @throws IllegalArgumentException    if any of the string parameters is null, empty or blank,
     *                                     or the priority of the rule is not within the expected range
     * @throws AccountNotFoundException    if the account does not exist
     * @throws FolderNotFoundException     if the folder does not exist
     * @throws RuleAlreadyDefinedException if the rule definition contains a rule *condition* that already exists,
     *                                     e.g. a rule definition contains `subject-includes` twice,
     *                                     or any other condition more than once.
     */
    void addRule(String accountName, String folderPath, String ruleDefinition, int priority);

    /**
     * The mail metadata has the following format (we always expect valid format of the mail metadata,
     * no validations are required):
     * sender: <sender-email>
     * subject: <subject>
     * recipients: <list-of-emails>
     * received: <LocalDateTime> - in format yyyy-MM-dd HH:mm
     * <p>
     * The order is not determined and the list might not be full. Example:
     * sender: testy@gmail.com
     * subject: Hello, MJT!
     * recipients: pesho@gmail.com, gosho@gmail.com,
     * received: 2022-12-08 14:14
     *
     * @param accountName  the recipient account
     * @param mailMetadata metadata, including the sender, all recipients, subject, and receiving time
     * @param mailContent  content of the mail
     * @throws IllegalArgumentException if any of the parameters is null, empty or blank
     * @throws AccountNotFoundException if the account does not exist
     */
    void receiveMail(String accountName, String mailMetadata, String mailContent);

    /**
     * Returns a collection of all mails contained directly in the provided folder.
     *
     * @param account    name of the selected account
     * @param folderPath full path of the folder
     * @return collections of mails available in the folder
     * @throws IllegalArgumentException if any of the parameters is null, empty or blank
     * @throws AccountNotFoundException if the account does not exist
     * @throws FolderNotFoundException  if the folder does not exist
     */
    Collection<Mail> getMailsFromFolder(String account, String folderPath);

    /**
     * Sends an email. This stores the mail into the sender's "/sent" folder.
     * For each recipient in the recipients email list in the metadata, if an account with this email exists,
     * a {@code receiveMail()} for this account, mail metadata and mail content is called.
     * If an account with the specified email does not exist, it is ignored.
     *
     * @param accountName  name of the sender
     * @param mailMetadata metadata of the mail. "sender" field should be included automatically
     *                     if missing or not correctly set
     * @param mailContent  content of the mail
     * @throws IllegalArgumentException if any of the parameters is null, empty or blank
     */
    void sendMail(String accountName, String mailMetadata, String mailContent);

}