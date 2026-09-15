# Briefly

Briefly is a local API that turns recent news into a short News Brief. You send a Query, Briefly
searches GNews for matching English Articles, and an OpenAI-compatible model writes a title, an
Overview, and one to five Highlights. Each Highlight lists the Source Articles meant to support it.
Domain terms are defined in [docs/CONTEXT.md](docs/CONTEXT.md).

## Requirements

- JDK 25 (the Maven Wrapper downloads Maven for you)
- A [GNews](https://gnews.io) API key
- An API key for an OpenAI-compatible Chat Completions provider, such as
  [Gemini](https://ai.google.dev/gemini-api/docs/api-key)

## Setup

Copy `.env.example` to `.env` and add both keys. Gemini's base URL and model are already filled in:

```dotenv
GNEWS_API_KEY=your-gnews-key
BRIEFLY_AI_API_KEY=your-gemini-key
BRIEFLY_AI_BASE_URL=https://generativelanguage.googleapis.com/v1beta/openai
BRIEFLY_AI_MODEL=gemini-3.7-flash
```

All four variables are required. An empty `BRIEFLY_AI_BASE_URL` sends AI requests to OpenAI, so
don't leave any of them blank. Briefly reads `.env` from the directory you start it in, which is the
project root with Maven. It's a Java properties file, so don't quote values. Environment variables
override it.

### Other AI providers

Set all three `BRIEFLY_AI_*` variables. The base URL includes the provider's path prefix, such as
`/v1`, but not `/chat/completions`. The model is the provider's API identifier, not its display name.

| Provider   | `BRIEFLY_AI_BASE_URL`                                     | Model IDs                                                                   |
|------------|-----------------------------------------------------------|-----------------------------------------------------------------------------|
| Gemini     | `https://generativelanguage.googleapis.com/v1beta/openai` | [Gemini models](https://ai.google.dev/gemini-api/docs/models)               |
| OpenAI     | `https://api.openai.com/v1`                               | [OpenAI models](https://developers.openai.com/api/docs/models)              |
| OpenRouter | `https://openrouter.ai/api/v1`                            | [OpenRouter models](https://openrouter.ai/models), with the provider prefix |
| Ollama     | `http://localhost:11434/v1`                               | Run `ollama list`                                                           |

Ollama ignores the API key, so any placeholder such as `ollama` works.

## Run

```bash
./mvnw spring-boot:run
```

Or build the JAR and run it from the directory that holds `.env`:

```bash
./mvnw -DskipTests package
java -jar target/briefly-0.0.1-SNAPSHOT.jar
```

On Windows, use `.\mvnw.cmd`. Briefly listens on port 8080. Set `SERVER_PORT` to change it.

Of the four variables, only a missing `BRIEFLY_AI_BASE_URL` stops startup. Briefly doesn't check the
keys or the model until the first News Brief request, which returns `502` if one is wrong.

Logs default to `WARN`. Set `LOGGING_LEVEL_IO_GITHUB_LEFPAP_BRIEFLY=INFO` to see request timings,
Article counts, and token usage. Every API response has an `X-Correlation-ID` header that matches
its log lines.

## API

Briefly serves its own API docs while it runs:

- [Swagger UI](http://localhost:8080/swagger-ui.html) describes every field and error, and lets you
  send requests. **Try it out** makes real calls that count against your GNews and AI quotas.
- The OpenAPI spec is at <http://localhost:8080/v3/api-docs>.

| Endpoint                         | Returns                                                      |
|----------------------------------|--------------------------------------------------------------|
| `POST /api/briefs/generate`      | A News Brief as JSON                                         |
| `POST /api/briefs/generate/html` | The same News Brief as an HTML page                          |
| `GET /actuator/health`           | Application status, without calling GNews or the AI provider |

### Generate a News Brief

Both generate endpoints take the same JSON body:

| Field   | Required | Description                                                          |
|---------|----------|----------------------------------------------------------------------|
| `query` | Yes      | The news you want, in plain text. Up to 200 characters.              |
| `from`  | No       | Start of the Reporting Window. Defaults to 24 hours before `to`.     |
| `to`    | No       | End of the Reporting Window. Defaults to the time of the request.    |

Timestamps are UTC, such as `2026-09-14T08:00:00Z`. They can't be in the future, and `from` can't be
after `to`.

```bash
curl -X POST http://localhost:8080/api/briefs/generate \
  -H "Content-Type: application/json" \
  -d '{"query": "European Union artificial intelligence regulation"}'
```

The response echoes the resolved `criteria` and adds a `title`, an `overview`, up to five
`highlights`, and the `sourceArticles` they cite. Shortened:

```json
{
  "criteria": {
    "query": "European Union artificial intelligence regulation",
    "from": "2025-05-01T09:15:30Z",
    "to": "2025-05-02T09:15:30Z"
  },
  "title": "EU moves to tighten AI oversight",
  "overview": "Lawmakers advanced new supervisory duties for high-risk AI systems, while industry groups warned about compliance costs.",
  "highlights": [
    {
      "text": "Parliament backed stricter supervision of high-risk AI systems, adding reporting duties for providers.",
      "citationIds": [1, 2]
    }
  ],
  "sourceArticles": [
    {
      "citationId": 1,
      "title": "Parliament backs stricter AI supervision",
      "description": "Lawmakers approved additional duties for providers of high-risk AI systems.",
      "url": "https://example.com/news/parliament-backs-ai-supervision",
      "publisher": { "name": "Example Times", "url": "https://example.com", "country": "be" },
      "metadata": { "imageUrl": "https://example.com/images/parliament.jpg", "publishedAt": "2025-05-01T18:42:00Z", "sourceLanguage": "en" }
    },
    {
      "citationId": 2,
      "title": "Industry warns over AI compliance deadlines",
      "description": "Trade bodies said the proposed timeline is unworkable for smaller providers.",
      "url": "https://example.org/news/industry-warns-ai-deadlines",
      "publisher": { "name": "Example Business Review", "url": "https://example.org", "country": "ie" },
      "metadata": { "imageUrl": "https://example.org/images/compliance.jpg", "publishedAt": "2025-05-02T06:10:00Z", "sourceLanguage": "en" }
    }
  ],
  "generatedAt": "2025-05-02T09:15:30Z"
}
```

Each number in `citationIds` matches a `citationId` in `sourceArticles`, which holds only the cited
Articles. Citation IDs mean nothing outside the response. `publisher.country` is where the Publisher
is based, not where the news happened.

### Get the News Brief as HTML

```bash
curl -X POST http://localhost:8080/api/briefs/generate/html \
  -H "Content-Type: application/json" \
  -d '{"query": "European Union artificial intelligence regulation"}' \
  -o brief.html
```

Open `brief.html` in a browser. Errors from this endpoint are still JSON.

![News Brief page showing the title, Brief Criteria, Overview, and a Highlight with its citations](docs/images/news-brief-page.png)

The page ends with a card for each Source Article:

![Source Article cards with Citation IDs, Publishers, titles, and descriptions](docs/images/news-brief-sources.png)

### Errors

Errors are [Problem Details](https://www.rfc-editor.org/rfc/rfc9457) with a stable `code`:

| Status | `code`                            | Cause                                                           |
|--------|-----------------------------------|-----------------------------------------------------------------|
| `400`  | `INVALID_REQUEST`                 | Invalid JSON or field. `errors` lists each problem.             |
| `422`  | `INSUFFICIENT_GENERATION_CONTEXT` | GNews found fewer than two Articles.                            |
| `502`  | `ARTICLE_SEARCH_FAILED`           | GNews failed, for example on a bad key, quota, or rate limit.   |
| `502`  | `BRIEF_GENERATION_FAILED`         | The AI call failed or its output was unusable.                  |
| `500`  | `INTERNAL_ERROR`                  | Unexpected error in Briefly.                                    |

## Limits

Search covers English Articles from any country, and every News Brief is written in English. Each
request makes one GNews search for up to 10 Articles, sorted by relevance. Briefly sends the Query
as typed. GNews treats quotes, `AND`, `OR`, and `NOT` as operators and requires special characters
to be quoted.

Nothing is stored or cached, so repeating a request calls both providers again and can return a
different News Brief. Briefly doesn't rate-limit, track quotas, or retry failed calls. The AI client
keeps Spring AI's defaults, though, which retry up to 3 times with a 60-second timeout per attempt.

The GNews free plan is for development and testing only, and has these limits:

- Articles appear 12 hours after publication, so the default 24-hour window covers news from 12 to
  24 hours ago.
- Search goes back 30 days.
- Article content is truncated. Briefly doesn't fetch publisher pages, so the model only sees each
  Article's title, description, and excerpt.
- 10 Articles per request, 100 requests per day (reset at 00:00 UTC), and 1 request per second.
  Going over returns `ARTICLE_SEARCH_FAILED`.

Citations show which Source Articles the model meant to support each Highlight. Briefly only checks
that the cited IDs exist and that at least two Articles are cited. It doesn't check that the
Articles say what the Highlight claims, so read them before you rely on a News Brief.
