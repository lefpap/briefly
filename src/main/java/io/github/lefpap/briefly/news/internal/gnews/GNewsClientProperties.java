package io.github.lefpap.briefly.news.internal.gnews;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties("gnews.client")
@Builder(toBuilder = true)
public record GNewsClientProperties(
    @NotNull URI baseUrl,
    @NotBlank String apiKey
) {
}
