package io.github.lefpap.briefly.briefs.internal.ai;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Stops startup when an AI provider setting is missing or blank.
 *
 * <p>Spring AI binds the same properties but accepts blank values without complaint: an empty API key
 * switches its client to unauthenticated requests, a blank base URL falls back to OpenAI's endpoint,
 * and a blank model falls back to Spring AI's default model. Binding them again here lets Bean
 * Validation reject them before a News Brief request reaches the wrong provider.
 */
@Validated
@ConfigurationProperties("spring.ai.openai")
public record AiProviderProperties(
    @NotBlank String baseUrl,
    @NotBlank String apiKey,
    @NotNull @Valid Chat chat
) {

    public record Chat(
        @NotBlank String model
    ) {
    }
}
