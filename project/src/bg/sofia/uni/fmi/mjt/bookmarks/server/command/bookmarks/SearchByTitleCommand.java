package bg.sofia.uni.fmi.mjt.bookmarks.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;

import java.util.List;

public class SearchByTitleCommand extends SearchCommand {

    private final String title;

    public SearchByTitleCommand(String title) {
        this.title = title;
        Nullable.throwIfNull(title);
    }

    @Override
    protected List<Bookmark> searchByCriteria() {
        return user.getBookmarks().stream().filter(x -> x.getTitle().toLowerCase().contains(title.toLowerCase()))
            .toList();
    }

}
