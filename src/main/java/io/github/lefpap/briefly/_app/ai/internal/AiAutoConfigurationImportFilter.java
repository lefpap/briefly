package io.github.lefpap.briefly._app.ai.internal;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Keeps starter-managed chat models while excluding unused provider capabilities.
 * Cloud providers require a non-blank common API key; Ollama needs no credentials.
 */
public class AiAutoConfigurationImportFilter implements AutoConfigurationImportFilter, EnvironmentAware {

    // Revisit these package boundaries and allowed classes when upgrading Spring AI.
    private static final String OPENAI_PACKAGE = "org.springframework.ai.model.openai.autoconfigure.";
    private static final String ANTHROPIC_PACKAGE = "org.springframework.ai.model.anthropic.autoconfigure.";
    private static final String GOOGLE_GENAI_PACKAGE = "org.springframework.ai.model.google.genai.autoconfigure.";
    private static final String OLLAMA_PACKAGE = "org.springframework.ai.model.ollama.autoconfigure.";

    private @Nullable Environment environment;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean @NonNull [] match(@Nullable String @NonNull [] candidates, @NonNull AutoConfigurationMetadata metadata) {
        boolean[] matches = new boolean[candidates.length];
        for (int i = 0; i < candidates.length; i++) {
            String candidate = candidates[i];
            matches[i] = candidate == null || isEnabled(candidate);
        }
        return matches;
    }

    private boolean isEnabled(String candidate) {
        if (candidate.startsWith(OPENAI_PACKAGE)) {
            return candidate.equals(OPENAI_PACKAGE + "OpenAiChatAutoConfiguration")
                && hasKey("spring.ai.openai.api-key");
        }
        if (candidate.startsWith(ANTHROPIC_PACKAGE)) {
            return candidate.equals(ANTHROPIC_PACKAGE + "AnthropicChatAutoConfiguration")
                && hasKey("spring.ai.anthropic.api-key");
        }
        if (candidate.startsWith(GOOGLE_GENAI_PACKAGE)) {
            return candidate.equals(GOOGLE_GENAI_PACKAGE + "chat.GoogleGenAiChatAutoConfiguration")
                && hasKey("spring.ai.google.genai.api-key");
        }
        if (candidate.startsWith(OLLAMA_PACKAGE)) {
            return candidate.equals(OLLAMA_PACKAGE + "OllamaChatAutoConfiguration")
                || candidate.equals(OLLAMA_PACKAGE + "OllamaApiAutoConfiguration");
        }
        return true;
    }

    private boolean hasKey(String property) {
        Assert.state(environment != null, "Environment must be set before filtering AI auto-configurations");
        return StringUtils.hasText(environment.getProperty(property));
    }
}
