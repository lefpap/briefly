package io.github.lefpap.briefly._app.observability.internal;

import io.github.lefpap.briefly._app.observability.api.HttpCallTimingInterceptor;
import org.springframework.ai.chat.client.ChatClientBuilderCustomizer;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class ObservabilityConfig {

    /**
     * Scoped to the Rest API endpoints so that health-check polling does not produce completion
     * logs, and ordered first so the correlation ID covers the whole request.
     */
    @Bean
    public FilterRegistrationBean<ObservabilityFilter> observabilityFilterRegistration() {
        var registration = new FilterRegistrationBean<>(new ObservabilityFilter());
        registration.addUrlPatterns("/api/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    /**
     * Applied to every auto-configured {@code RestClient.Builder}, so an outbound call is timed
     * without the client having to know it is being measured. Calls are told apart by the name the
     * client gives itself with {@link HttpCallTimingInterceptor#named}, and by host otherwise.
     */
    @Bean
    public RestClientCustomizer httpCallTimingCustomizer() {
        return builder -> builder.requestInterceptor(new HttpCallTimingInterceptor());
    }

    /**
     * Applied to the auto-configured {@code ChatClient.Builder}, so the AI provider call is timed
     * without the generation service having to know it is being measured.
     */
    @Bean
    public ChatClientBuilderCustomizer aiCallTimingCustomizer() {
        return builder -> builder.defaultAdvisors(new AiCallTimingAdvisor());
    }

    /**
     * Applied the same way, so what a News Brief costs in tokens is reported next to what it costs
     * in time.
     */
    @Bean
    public ChatClientBuilderCustomizer aiCallUsageCustomizer() {
        return builder -> builder.defaultAdvisors(new AiCallUsageAdvisor());
    }
}
