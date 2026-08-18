package io.github.lefpap.briefly.lib.gnews;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties("gnews.client")
public record GNewsClientProperties(
    @NotNull URI baseUrl,
    @NotBlank String apiKey
) {
}
