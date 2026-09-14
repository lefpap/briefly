package io.github.lefpap.briefly.news.api.exception;

public class ArticleSearchException extends RuntimeException {

    public ArticleSearchException(String message) {
        super(message);
    }

    public ArticleSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
