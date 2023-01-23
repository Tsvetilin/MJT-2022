package bg.sofia.uni.fmi.mjt.cocktail.server.command;

import bg.sofia.uni.fmi.mjt.cocktail.server.storage.CocktailStorage;

public class CommandExecutor {

    private static final String INVALID_ARGS_COUNT_MESSAGE_FORMAT =
        "Invalid count of arguments: \"%s\" expects %d arguments.";

    private static final String GET = "get";
    private static final String CREATE = "create";
    CocktailStorage storage;

    public CommandExecutor(CocktailStorage storage) {
        this.storage = storage;
    }

    public String execute(Command cmd) {
        return switch (cmd.command()) {
            case GET -> get(cmd.arguments());
            case CREATE -> create(cmd.arguments());
            default -> "Unknown command";
        };
    }

    private String get(String... args) {
        return "";
    }

    private String create(String... args){
        return "";
    }

}
