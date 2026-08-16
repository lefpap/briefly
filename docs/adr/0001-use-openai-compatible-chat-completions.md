# Use OpenAI-compatible Chat Completions

Briefly uses Spring AI's OpenAI chat integration against a configurable OpenAI-compatible base URL, API key, and model, with Gemini as the default provider. This lets operators change compatible providers without changing the News Brief generation code, at the cost of avoiding provider-specific features and relying on application validation because protocol and structured-output support vary between providers.
