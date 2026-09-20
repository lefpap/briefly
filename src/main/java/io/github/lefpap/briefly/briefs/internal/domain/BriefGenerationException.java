package io.github.lefpap.briefly.briefs.internal.domain;

public class BriefGenerationException extends RuntimeException {
    public BriefGenerationException(String message) {
        super(message);
    }

    public BriefGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
