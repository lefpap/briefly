package io.github.lefpap.briefly._app.ai.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(AgentAiProperties.CONFIGURATION_PREFIX)
public record AgentAiProperties(Map<String, Agent> agents) {

    public static final String CONFIGURATION_PREFIX = "app.ai";

    public AgentAiProperties {
        agents = agents == null ? Map.of() : Map.copyOf(agents);
    }

    public record Agent(String model) {
    }

}
