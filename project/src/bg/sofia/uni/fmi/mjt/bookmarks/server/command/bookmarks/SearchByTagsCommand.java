package bg.sofia.uni.fmi.mjt.bookmarks.server.command.bookmarks;

import bg.sofia.uni.fmi.mjt.bookmarks.server.models.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.utils.Nullable;

import java.util.List;

public class SearchByTagsCommand extends SearchCommand {
    private final List<String> tags;

    public SearchByTagsCommand(List<String> tags) {
        this.tags = tags;
        Nullable.throwIfNull(tags);
        if (tags.size() == 0) {
            throw new IllegalArgumentException("Tags cannot be 0.");
        }
    }

    @Override
    protected List<Bookmark> searchByCriteria() {
        return user.getBookmarks().stream().filter(x -> x.getKeywords().stream().anyMatch(tags::contains))
            .toList();
    }
}
