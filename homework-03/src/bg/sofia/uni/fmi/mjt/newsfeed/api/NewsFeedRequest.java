package bg.sofia.uni.fmi.mjt.newsfeed.api;

import bg.sofia.uni.fmi.mjt.newsfeed.utils.Iterator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class NewsFeedRequest {

    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "newsapi.org";
    private static final String API_ENDPOINT_PATH = "/v2/top-headlines";

    private static final String KEYWORDS_PARAM = "q";
    private static final String COUNTRY_PARAM = "country";
    private static final String CATEGORY_PARAM = "category";
    private static final String PAGE_PARAM = "page";
    private static final String PAGE_SIZE_PARAM = "pageSize";


    private static final int ELEMENTS_ON_PAGE = 50;
    private static final int DEFAULT_PAGES_COUNT = 2;

    private static final int DEFAULT_PAGE_NUMBER = 1;

    private final int desiredResultsCount;
    private final List<String> keywords;
    private final NewsFeedCategory category;
    private final NewsFeedCountry country;

    private NewsFeedRequest(NewsFeedRequestBuilder builder) {
        this.keywords = builder.getKeywords();
        this.category = builder.getCategory();
        this.country = builder.getCountry();
        this.desiredResultsCount = builder.getDesiredCount();
    }

    public static NewsFeedRequestBuilder newRequest(String... keywords) {
        return new NewsFeedRequestBuilder(keywords);
    }

    public URI uri() {
        return uri(DEFAULT_PAGE_NUMBER);
    }

    public URI uri(int page) {
        try {
            return new URI(
                API_ENDPOINT_SCHEME,
                API_ENDPOINT_HOST,
                API_ENDPOINT_PATH,
                getEndpointQuery(page),
                null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void addQueryParam(StringBuilder builder, String paramName, String paramValue) {
        builder.append(paramName);
        builder.append("=");
        builder.append(paramValue);
        builder.append("&");
    }

    private String getEndpointQuery(int pageNumber) {
        StringBuilder sb = new StringBuilder();
        addQueryParam(sb, KEYWORDS_PARAM, String.join(", ", keywords));

        if (country != NewsFeedCountry.DEFAULT) {
            addQueryParam(sb, COUNTRY_PARAM, country.country());
        }

        if (category != NewsFeedCategory.DEFAULT) {
            addQueryParam(sb, CATEGORY_PARAM, category.category());
        }

        addQueryParam(sb, PAGE_PARAM, String.valueOf(pageNumber));
        addQueryParam(sb, PAGE_SIZE_PARAM, String.valueOf(ELEMENTS_ON_PAGE));

        return sb.toString();
    }

    public Iterator<URI> getIterator(int elementsTotal) {
        if (desiredResultsCount < elementsTotal) {
            elementsTotal = desiredResultsCount;
        }

        return new RequestUriIterator(Math.ceilDiv(elementsTotal, ELEMENTS_ON_PAGE));
    }

    private class RequestUriIterator implements Iterator<URI> {

        private final int pages;
        private int currentPage = 2;

        private RequestUriIterator(int pages) {
            this.pages = pages;
        }

        @Override
        public boolean hasNext() {
            return currentPage <= pages;
        }

        @Override
        public URI next() {
            return uri(currentPage++);
        }
    }

    public static class NewsFeedRequestBuilder {

        private final List<String> keywords;
        private NewsFeedCategory category = NewsFeedCategory.DEFAULT;
        private NewsFeedCountry country = NewsFeedCountry.DEFAULT;
        private int desiredCount = DEFAULT_PAGES_COUNT * ELEMENTS_ON_PAGE;

        public List<String> getKeywords() {
            return keywords;
        }

        public NewsFeedCategory getCategory() {
            return category;
        }

        public int getDesiredCount() {
            return desiredCount;
        }

        public NewsFeedCountry getCountry() {
            return country;
        }

        private NewsFeedRequestBuilder(String... keywords) {
            this.keywords = new ArrayList<>();
            this.keywords.addAll(List.of(keywords));
        }

        public NewsFeedRequestBuilder forCategory(NewsFeedCategory category) {
            this.category = category;
            return this;
        }

        public NewsFeedRequestBuilder fromCountry(NewsFeedCountry country) {
            this.country = country;
            return this;
        }

        public NewsFeedRequestBuilder withKeywords(String... keywords) {
            this.keywords.addAll(List.of(keywords));
            return this;
        }

        public NewsFeedRequestBuilder getTopNNews(int n) {
            if (n > 0) {
                desiredCount = n;
            }

            return this;
        }

        public NewsFeedRequest build() {
            return new NewsFeedRequest(this);
        }

    }
}
