# Use native Spring AI integrations

Supersedes [ADR-0001](0001-use-openai-compatible-chat-completions.md).

Briefly retains Spring AI's native OpenAI, Anthropic, Google GenAI, and Ollama starters. Each agent selects a provider and model through `app.ai.agents.<agent-id>.model=provider/model` in `application.yaml`, splitting at the first slash. News Brief generation uses the `brief-generation` agent configuration.

A startup auto-configuration import filter excludes providers with blank API keys and disables unused embedding, image, audio, and moderation configurations, including their connection configurations. Available chat models remain Spring-managed singletons. This application supports API-key cloud connections and local Ollama; Vertex AI and alternate authentication modes are outside this decision. The global `spring.ai.model.chat` selector stays unset.

`AiChatClientBuilderFactory` parses and validates an agent's provider/model string, selects a provider model, and returns a fresh builder with agent-specific options. It preserves Spring's client customizers and observation configuration. Agents resolve their builders during execution so invalid selections and missing credentials are reported only when needed. Credentials and mappings require a restart to change.

This replaces the earlier single-provider startup selection with per-agent routing while retaining native protocol support and starter-managed model configuration. It adds a small import filter and factory instead of manually constructing SDK clients. Model access and generated-output quality still require verification against the selected provider.
