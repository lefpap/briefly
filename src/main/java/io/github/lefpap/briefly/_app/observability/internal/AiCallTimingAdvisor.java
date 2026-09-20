package io.github.lefpap.briefly._app.observability.internal;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.core.Ordered;

/**
 * Logs how long the AI provider call takes, under the correlation ID of the request that made it.
 *
 * <p>Ordered immediately inside {@code ChatModelCallAdvisor}, the terminal advisor that performs the
 * model call, so the duration is the provider round trip rather than the round trip plus prompt
 * formatting and generated-response validation.
 *
 * <p>The advice deliberately reads nothing out of the request or the response: the prompt and the
 * generated News Brief must not reach the log.
 */
@Slf4j
public class AiCallTimingAdvisor implements CallAdvisor {

    @Override
    public @NonNull ChatClientResponse adviseCall(@NonNull ChatClientRequest request, @NonNull CallAdvisorChain chain) {
        long startedAt = System.nanoTime();
        try {
            ChatClientResponse response = chain.nextCall(request);
            log.info("AI call completed duration={}ms", elapsedMillis(startedAt));
            return response;
        } catch (RuntimeException exception) {
            // Only the type: the message can carry prompt or generated content.
            log.warn(
                "AI call failed duration={}ms exception={}",
                elapsedMillis(startedAt),
                exception.getClass().getName()
            );
            throw exception;
        }
    }

    @Override
    public @NonNull String getName() {
        return "aiCallTiming";
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }

    private static long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
