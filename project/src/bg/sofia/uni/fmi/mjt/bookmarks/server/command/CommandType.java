package bg.sofia.uni.fmi.mjt.bookmarks.server.command;

public enum CommandType {
    HELP("help"),
    UNKNOWN("unknown"),
    REGISTER("register"),
    LOGIN("login"),
    LOGOUT("logout"),
    NEW_GROUP("new-group"),
    ADD_TO("add-to"),
    REMOVE_FROM("remove-from"),
    LIST("list"),
    SEARCH("search"),
    CLEANUP("cleanup"),
    IMPORT_FROM_CHROME("import-from-chrome");

    private final String name;

    CommandType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
