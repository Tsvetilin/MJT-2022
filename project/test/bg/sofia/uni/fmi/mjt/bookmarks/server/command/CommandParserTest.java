package bg.sofia.uni.fmi.mjt.bookmarks.server.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandParserTest {

    @Test
    void testParseEmptyCommand() {
        Command cmd = CommandParser.parse("  ");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map invalid command to Unknown cmd.");
    }

    @Test
    void testParseInvalidCommand() {
        Command cmd = CommandParser.parse("invalid command");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map invalid command to Unknown cmd.");
    }

    @Test
    void testParseHelpCommand() {
        Command cmd = CommandParser.parse("help");
        assertEquals(CommandType.HELP, cmd.getType(), "Should map help command to Help cmd.");
    }

    @Test
    void testParseHelpCommandInvalid() {
        Command cmd = CommandParser.parse("help with args");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map help command to Unknown cmd.");
    }

    @Test
    void testParseLoginCommand() {
        Command cmd = CommandParser.parse("login username pass");
        assertEquals(CommandType.LOGIN, cmd.getType(), "Should map login command to Login cmd.");
    }

    @Test
    void testParseInvalidLoginCommand() {
        Command cmd = CommandParser.parse("login with different amount of args");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map invalid command to Unknown cmd.");
    }

    @Test
    void testParseRegisterCommand() {
        Command cmd = CommandParser.parse("register un pw");
        assertEquals(CommandType.REGISTER, cmd.getType(), "Should map register command to Register cmd.");
    }

    @Test
    void testParseInvalidRegisterCommand() {
        Command cmd = CommandParser.parse("register noargs");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map invalid command to Unknown cmd.");
    }

    @Test
    void testParseLogoutCommand() {
        Command cmd = CommandParser.parse("logout");
        assertEquals(CommandType.LOGOUT, cmd.getType(), "Should map logout command to Logout cmd.");
    }

    @Test
    void testParseInvalidLogoutCommand() {
        Command cmd = CommandParser.parse("logout with rnd params");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map invalid command to Unknown cmd.");
    }

    @Test
    void testParseAddToCommand() {
        Command cmd = CommandParser.parse("add-to group url");
        assertEquals(CommandType.ADD_TO, cmd.getType(), "Should map command to AddTo cmd.");
    }

    @Test
    void testParseAddToInvalidCommand() {
        Command cmd = CommandParser.parse("add-to with maaany maany args");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map invalid command to Unknown cmd.");
    }

    @Test
    void testParseImportFromChromeCommand() {
        Command cmd = CommandParser.parse("import-from-chrome url");
        assertEquals(CommandType.IMPORT_FROM_CHROME, cmd.getType(), "Should map command to ImportFromChrome cmd.");
    }

    @Test
    void testParseImportFromChromeInvalidCommand() {
        Command cmd = CommandParser.parse("import-from-chrome");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map invalid command to Unknown cmd.");
    }


    @Test
    void testParseListInvalidArgsCommand() {
        Command cmd = CommandParser.parse("list args");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map command to Unknown cmd.");
    }

    @Test
    void testParseListGroupCommand() {
        Command cmd = CommandParser.parse("list --group-name groupname");
        assertEquals(CommandType.LIST, cmd.getType(), "Should map command to List cmd.");
    }

    @Test
    void testParseListGroupInvalidArgsCommand() {
        Command cmd = CommandParser.parse("list--groupname groupname and other args");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map command to Unknown cmd.");
    }

    @Test
    void testParseListGroupNoGroupCommand() {
        Command cmd = CommandParser.parse("list--groupname ");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map command to Unknown cmd.");
    }


    @Test
    void testParseNewGroupCommand() {
        Command cmd = CommandParser.parse("new-group group");
        assertEquals(CommandType.NEW_GROUP, cmd.getType(), "Should map command to NewGroup cmd.");
    }


    @Test
    void testParseNewGroupInvalidCommand() {
        Command cmd = CommandParser.parse("new-group too many args");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map invalid command to Unknown cmd.");
    }


    @Test
    void testParseRemoveFromCommand() {
        Command cmd = CommandParser.parse("remove-from group url");
        assertEquals(CommandType.REMOVE_FROM, cmd.getType(), "Should map command to RemoveFrom cmd.");
    }


    @Test
    void testParseRemoveFromInvalidCommand() {
        Command cmd = CommandParser.parse("remove-from wherewhat");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map invalid command to Unknown cmd.");
    }

    @Test
    void testParseSearchTitleCommand() {
        Command cmd = CommandParser.parse("search --title title");
        assertEquals(CommandType.SEARCH, cmd.getType(), "Should map command to Search cmd.");
    }

    @Test
    void testParseSearchTagCommand() {
        Command cmd = CommandParser.parse("search --tags tag");
        assertEquals(CommandType.SEARCH, cmd.getType(), "Should map command to Search cmd.");
    }

    @Test
    void testParseSearchTagsManyCommand() {
        Command cmd = CommandParser.parse("search --tags title tag another");
        assertEquals(CommandType.SEARCH, cmd.getType(), "Should map command to Search cmd.");
    }

    @Test
    void testParseInvalidSearchCommand() {
        Command cmd = CommandParser.parse("search   ");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map invalid command to Unknown cmd.");
    }

    @Test
    void testParseInvalidSearchTitleCommand() {
        Command cmd = CommandParser.parse("search --title");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map invalid command to Unknown cmd.");
    }

    @Test
    void testParseInvalidSearchTagsCommand() {
        Command cmd = CommandParser.parse("search --tags ");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Should map invalid command to Unknown cmd.");
    }

}
