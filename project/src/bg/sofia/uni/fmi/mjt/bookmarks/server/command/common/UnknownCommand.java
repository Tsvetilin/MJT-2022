package bg.sofia.uni.fmi.mjt.bookmarks.server.command.common;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandBase;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandType;

public class UnknownCommand extends CommandBase {

    private static final String DEFAULT_MESSAGE = "Unknown command specified. Use command 'help' for more info.";
    private final String message;

    public UnknownCommand() {
        this(DEFAULT_MESSAGE);
    }

    public UnknownCommand(String message) {
        this.message = message;
    }

    @Override
    public Response execute() {
        return new Response(message, ResponseStatus.ERROR);
    }


    @Override
    public CommandType getType() {
        return CommandType.UNKNOWN;
    }
}
