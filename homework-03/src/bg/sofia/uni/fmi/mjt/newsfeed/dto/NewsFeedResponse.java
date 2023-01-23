package bg.sofia.uni.fmi.mjt.newsfeed.dto;

import java.util.List;

public record NewsFeedResponse(String status, String code, String message, int totalResults, List<Article> articles) {
}

