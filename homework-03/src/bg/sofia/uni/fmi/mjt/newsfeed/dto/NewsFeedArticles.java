package bg.sofia.uni.fmi.mjt.newsfeed.dto;

import java.util.List;

public record NewsFeedArticles(int totalResults, List<Article> articles) {

}
