package io.github.lefpap.briefly._app.observability.internal;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.Ordered;

/**
 * Logs how many tokens the AI provider call consumed, under the correlation ID of the request that
 * made it. What Spring AI calls prompt and completion tokens is logged as input and output, the
 * names the providers bill under.
 *
 * <p>Ordered immediately outside {@link AiCallTimingAdvisor}, so the two lines a call produces are
 * written next to each other, duration first.
 *
 * <p>The advice deliberately reads nothing out of the request or the response beyond the token
 * counts: the prompt and the generated News Brief must not reach the log. A provider that reports no
 * usage leaves {@link org.springframework.ai.chat.metadata.EmptyUsage}, whose counts are zero, so
 * the absence is reported at {@code DEBUG} rather than logged as a free call.
 */
@Slf4j
public class AiCallUsageAdvisor implements CallAdvisor {

    @Override
    public @NonNull ChatClientResponse adviseCall(@NonNull ChatClientRequest request, CallAdvisorChain chain) {
        ChatClientResponse response = chain.nextCall(request);
        logUsage(response);
        return response;
    }

    @Override
    public @NonNull String getName() {
        return "aiCallUsage";
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 2;
    }

    private static void logUsage(ChatClientResponse response) {
        ChatResponse chatResponse = response.chatResponse();
        if (chatResponse == null) {
            log.debug("AI call reported no token usage: the response carries no provider metadata");
            return;
        }

        Usage usage = chatResponse.getMetadata().getUsage();
        Integer totalTokens = usage.getTotalTokens();
        if (totalTokens == 0) {
            log.debug("AI call reported no token usage: the provider returned none");
            return;
        }

        log.info(
            "AI call usage inputTokens={} outputTokens={} totalTokens={}",
            usage.getPromptTokens(),
            usage.getCompletionTokens(),
            totalTokens
        );
    }
}
