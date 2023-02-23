package bg.sofia.uni.fmi.mjt.bookmarks.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.bookmarks.contracts.Response;
import bg.sofia.uni.fmi.mjt.bookmarks.contracts.ResponseStatus;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.AuthenticatedCommand;
import bg.sofia.uni.fmi.mjt.bookmarks.server.command.CommandType;
import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Group;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.IdGenerator;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;

public class NewGroupCommand extends AuthenticatedCommand {

    private final String group;

    public NewGroupCommand(String group) {
        this.group = group;
        Nullable.throwIfNull(group);
    }

    @Override
    protected Response authenticatedExecute() {

        if (user.getGroups().stream().map(Group::getName).anyMatch(x -> x.equals(group))) {
            logger.logInfo("User " + user.getUsername() + " tried to create already existing group: " + group);
            return new Response("Group already exits.", ResponseStatus.ERROR);
        }

        context.groups().add(new Group(IdGenerator.generateId(), group, user));
        logger.logInfo("Created group " + group + " for user " + user.getUsername());

        return new Response("Successfully created group.", ResponseStatus.OK);
    }


    @Override
    public CommandType getType() {
        return CommandType.NEW_GROUP;
    }
}
