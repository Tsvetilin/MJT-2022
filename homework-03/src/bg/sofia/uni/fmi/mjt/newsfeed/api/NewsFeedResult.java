package bg.sofia.uni.fmi.mjt.newsfeed.api;

import bg.sofia.uni.fmi.mjt.newsfeed.dto.Article;

import java.util.List;

public record NewsFeedResult(List<Article> articles) {
}
