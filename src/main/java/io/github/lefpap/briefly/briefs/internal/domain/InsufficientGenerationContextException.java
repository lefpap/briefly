package io.github.lefpap.briefly.briefs.internal.domain;

public class InsufficientGenerationContextException extends RuntimeException {

    public InsufficientGenerationContextException(String message) {
        super(message);
    }
}
