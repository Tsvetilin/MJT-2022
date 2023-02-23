package bg.sofia.uni.fmi.mjt.bookmarks.server.command.common;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandBase;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandType;

public class HelpCommand extends CommandBase {

    private static final String HELP_PROMPT = """
        Available commands:
        register <username> <password> - register with your credentials
        login <username> <password> - log in with your credentials
        logout - log out from the server
        new-group <name> - creates new group
        add-to <group> <url> {--shorten} - add bookmark to group with option to shorten the link
        list {--group-name <group>} - list all bookmarks with option to show only certain group
        search (--title <title> | --tags <tag> [<tag> ...]) - search by title or one or multiple tags
        remove-from <group> <url> - remove bookmark from group
        """;

    @Override
    public Response execute() {
        return new Response(HELP_PROMPT, ResponseStatus.OK);
    }

    @Override
    public CommandType getType() {
        return CommandType.HELP;
    }
}