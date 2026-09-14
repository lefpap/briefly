package io.github.lefpap.briefly.news.internal.gnews;

class GNewsClientException extends RuntimeException {

    GNewsClientException(String message) {
        super(message);
    }

    GNewsClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
