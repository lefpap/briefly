package io.github.lefpap.briefly._app.ai.api;

import io.github.lefpap.briefly._app.ai.internal.AgentAiProperties;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.observation.AdvisorObservationConvention;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientBuilderConfigurer;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AiChatClientBuilderFactory {

    private static final String AGENT_PLACEHOLDER = "<agent>";
    private static final String AGENT_CONFIGURATION_PATH = AgentAiProperties.CONFIGURATION_PREFIX + ".agents." + AGENT_PLACEHOLDER + ".model";

    private static final String OPENAI_PROVIDER = "openai";
    private static final String ANTHROPIC_PROVIDER = "anthropic";
    private static final String GOOGLE_GENAI_PROVIDER = "google-genai";
    private static final String OLLAMA_PROVIDER = "ollama";

    private final AgentAiProperties properties;

    private final ObjectProvider<OpenAiChatModel> openai;
    private final ObjectProvider<AnthropicChatModel> anthropic;
    private final ObjectProvider<GoogleGenAiChatModel> google;
    private final ObjectProvider<OllamaChatModel> ollama;

    private final ChatClientBuilderConfigurer configurer;
    private final ObjectProvider<ObservationRegistry> observations;
    private final ObjectProvider<ChatClientObservationConvention> clientConvention;
    private final ObjectProvider<AdvisorObservationConvention> advisorConvention;
    private final ObjectProvider<ToolCallingAdvisor.Builder<?>> toolCalling;

    public ChatClient.Builder create(String agentId) {
        var agent = properties.agents().get(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("Unknown AI agent '" + agentId
                + "'; configure " + AgentAiProperties.CONFIGURATION_PREFIX + ".agents." + agentId + ".model");
        }
        ModelSelection selection = parseSelection(agentId, agent.model());
        ChatModel model = resolveChatModel(agentId, selection.provider());
        var builder = ChatClient.builder(
            model,
            observations.getIfUnique(() -> ObservationRegistry.NOOP),
            clientConvention.getIfUnique(),
            advisorConvention.getIfUnique(),
            toolCalling.getIfAvailable()
        );

        return configurer.configure(builder)
            .defaultOptions(ChatOptions.builder().model(selection.modelName()));
    }

    private static ModelSelection parseSelection(String agentId, String model) {
        final String path = agentConfigurationPath(agentId);
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException(path + " must be non-blank");
        }

        int slash = model.indexOf('/');
        if (slash <= 0 || slash == model.length() - 1
            || model.codePoints().anyMatch(Character::isWhitespace)
            || model.charAt(slash + 1) == '/') {
            throw new IllegalArgumentException(path + " must use provider/model without whitespace");
        }
        String provider = model.substring(0, slash);
        String modelName = model.substring(slash + 1);
        return new ModelSelection(provider, modelName);
    }

    private @NonNull ChatModel resolveChatModel(String agentId, String provider) {
        final String path = agentConfigurationPath(agentId);
        ChatModel model = switch (provider) {
            case OPENAI_PROVIDER -> openai.getIfAvailable();
            case ANTHROPIC_PROVIDER -> anthropic.getIfAvailable();
            case GOOGLE_GENAI_PROVIDER -> google.getIfAvailable();
            case OLLAMA_PROVIDER -> ollama.getIfAvailable();
            default -> {
                var supported = List.of(OPENAI_PROVIDER, ANTHROPIC_PROVIDER, GOOGLE_GENAI_PROVIDER, OLLAMA_PROVIDER);
                throw new IllegalArgumentException("Unsupported AI provider '" + provider
                    + "' in " + path + "; expected " + supported);
            }
        };

        if (model == null) {
            throw new IllegalStateException("Provider '" + provider + "' selected by " + path
                + " is unavailable; check its credentials, keep its starter enabled, and leave spring.ai.model.chat unset");
        }
        return model;
    }

    private static String agentConfigurationPath(String agentId) {
        return AgentAiProperties.CONFIGURATION_PREFIX + ".agents." + agentId + ".model";
    }


    private record ModelSelection(
        String provider,
        String modelName
    ) {
    }
}
