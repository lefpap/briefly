# Feature-oriented package structure without Spring Modulith

Research date: 2026-09-17. Framework claims were checked against the versions this project resolves — Spring Boot 4.1.0, Spring Framework 7.0.8, Java 25 — using the sources jars in the local Maven repository, alongside the official documentation linked below. Companion to [spring-modulith-package-structure.md](spring-modulith-package-structure.md), which covers what changes if Modulith is adopted. This note proposes structure only: no application code, dependency, or build file was changed.

## Summary

Keep business capabilities at the top level of the root application package, pick one arrangement for the inside of a capability, and spend the effort on shrinking each package's public surface rather than on package names.

- Spring Boot recommends exactly the top-level arrangement Briefly already has, and prescribes nothing below it. [Spring Boot: Structuring Your Code](https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html)
- Three arrangements for the inside of a capability are published and defensible: a flat feature package, a contract at the feature root with concern sub-packages, and ports and adapters. Briefly uses the second, with one extra level (`internal`).
- Nested packages are documentation, not encapsulation. Java defines no access relationship between `briefs` and `briefs.internal`, so types under `internal` must be public and are then public to the whole program.
- 36 of Briefly's 38 production types are public. 11 of them have no consumer outside their own package today and can become package-private without moving a file; 5 more become hideable if the `briefs` domain sub-packages are merged. That is compiler-enforced encapsulation available at no cost, and it does not depend on any naming decision.

## What the language and the framework actually give you

Spring Boot recommends placing the main application class in a root package above the code and grouping code by capability; its own example is `customer` and `order` packages holding the controller, service, repository, and entity of each. It prescribes nothing about the inside of those packages and points at Spring Modulith for enforcement. [Spring Boot: Structuring Your Code](https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html)

The language gives one boundary only, and it is the package, not the package tree. From JLS §7.1: the hierarchical naming structure "has no significance in itself other than the prohibition against a package having a subpackage with the same simple name as a top level class or interface declared in that package", and "code in a package named `oliver.twist` has no better access to the classes and interfaces declared within package `oliver` than code in any other package". [JLS §7.1](https://docs.oracle.com/javase/specs/jls/se21/html/jls-7.html)

That is the whole reason `briefs.internal.domain.model.Brief` is public: `briefs.internal.web` is a different package, so nothing weaker than `public` would let the controller see it. Spring Modulith's own documentation states the consequence plainly for its `order.internal` example: "Note how `SomethingOrderInternal` is a public type... This unfortunately means that it can also be referred to from other packages such as the `inventory` one. In this case, the Java compiler is not of much use to prevent these illegal references." [Spring Modulith: Fundamentals](https://docs.spring.io/spring-modulith/reference/fundamentals.html)

So the `api` / `internal` vocabulary Briefly borrowed is a tooling convention. It reads as intent and it is what Modulith's verifier keys on, but on its own it is enforced by nobody.

## Three arrangements for the inside of a capability

**A. Flat feature package.** All of a capability's code in one package, the contract public, everything else package-private. This is Modulith's "simple" module and, independently, the arrangement both Oliver Drotbohm and Simon Brown argue for. Drotbohm's recommendation: "Move the vertical slices into the focus of the package naming and try to model them in a way that the public API of a slice is as tiny as possible" — his point being that Java developers "usually just skip packages as means to control visibility of types". [Whoops! Where did my architecture go?](http://odrotbohm.github.io/2013/01/whoops-where-did-my-architecture-go/) Brown reaches the same place from the component angle: only the component interface is public, the rest is package-private, and "you can rely on the compiler to enforce this architectural principle" instead of on review discipline. [Modular monolith and "package by component"](https://simonbrown.je/modular-monolith/)

Cost: one package holds every concern of the capability, and file navigation is the only structure you get. Briefly's `briefs` capability would be 19 types in one package.

**B. Contract at the feature root, concerns in sub-packages.** Public contract types sit directly in `news`; implementation concerns sit in `news.gnews`, `news.web`, and so on. This is Modulith's "advanced" module — base package is the API package, every sub-package is internal — and the arrangement the Modulith maintainer endorses for internal technical layering, controllers and HTTP DTOs included. [Spring Modulith: Fundamentals](https://docs.spring.io/spring-modulith/reference/fundamentals.html), [maintainer's answer on project structure](https://github.com/spring-projects/spring-modulith/discussions/210)

The sub-packages need not be layers. Drotbohm's CQRS sample splits a capability into `command` and `query` and notes that "Almost all code within the `command` and `query` packages is package private, except the types in `command` that are needed by the query side" — concern sub-packages plus visibility applied deliberately inside the capability. [cqrs-spring-modulith](https://github.com/odrotbohm/cqrs-spring-modulith)

Cost: every type a sibling sub-package needs must be public, so the compiler stops helping at the capability's edge. Without a verifier, the boundary holds by convention.

**C. Ports and adapters inside the feature.** `news` keeps a domain core and names its outbound adapters explicitly (`news.port`, `news.adapter.gnews`, or the equivalent). The intent is Cockburn's: "Allow an application to equally be driven by users, programs, automated test or batch scripts, and to be developed and tested in isolation from its eventual run-time devices and databases", with the rule that "code pertaining to the *inside* part should not leak into the *outside* part". [Hexagonal architecture](https://alistair.cockburn.us/hexagonal-architecture/)

This pays for itself when a capability has several drivers or several interchangeable providers. Briefly's `news` already is a port and one adapter: `ArticleSearchService` plus a GNews implementation. Naming the packages `port` and `adapter` would add vocabulary without adding a boundary that B does not already give.

| | Enforced by | Reads as | Fits when |
|---|---|---|---|
| A. Flat feature | Compiler | One capability, tiny API | The capability is small, or its API must be provably tiny |
| B. Root contract + concerns | Convention (+ optional verifier) | Capability with internal concerns | Several concerns per capability, one team, small codebase |
| C. Ports and adapters | Convention (+ optional verifier) | Replaceable technology | Multiple adapters, or adapters swapped in tests |

A and B combine: the contract in the feature root is the public part, and any sub-package whose types nothing outside it needs can be fully package-private. Briefly's `news.internal.gnews` is exactly that case.

## What enforces the boundary: a cost ladder

1. **Naming only** — zero cost, zero enforcement. This is Briefly today.
2. **Java visibility** — free, checked by `javac` on every build, and the only option on this list that needs no test infrastructure. It only works within one package, so it constrains how you arrange concerns. Spring does not stand in the way: bean instantiation goes through `BeanUtils.instantiateClass`, which calls `ReflectionUtils.makeAccessible(ctor)`, `SpringFactoriesLoader` does the same for factory types such as `EnvironmentPostProcessor`, and SpEL calls it for both property access and method invocation, which covers the Thymeleaf `@briefTemplateSupport.timestamp(...)` calls in `brief.html`. (Verified in the 7.0.8 sources jars: `BeanUtils:186`, `SpringFactoriesLoader:369`, `ReflectivePropertyAccessor:192`, `ReflectiveMethodExecutor:113`.)
3. **ArchUnit** — a test-scoped dependency and a handful of rules. It checks what visibility cannot: that no other capability reaches into a sub-package, and that capabilities stay acyclic.

   ```java
   noClasses().that().resideInAPackage("..source..")
       .should().dependOnClassesThat().resideInAPackage("..foo..")

   SlicesRuleDefinition.slices().matching("..briefly.(*)..").should().beFreeOfCycles()
   ```

   Rules run under `@AnalyzeClasses` / `@ArchTest`, and `FreezingArchRule.freeze(rule)` records today's violations so a rule can be adopted before the code satisfies it. [ArchUnit user guide](https://www.archunit.org/userguide/html/000_Index.html) Briefly defers tests (`AGENTS.md`), so this step is available but not yet open.
4. **Spring Modulith verification** — the same test-time mechanism plus a module model, named interfaces, and documentation generation. Covered in the [companion note](spring-modulith-package-structure.md).
5. **Build modules or JPMS** — compile-time enforcement across capabilities, at the cost of a multi-module build or a module path. Spring is not a JPMS citizen yet: `spring-core-7.0.8.jar` ships `Automatic-Module-Name: spring.core` and contains no `module-info.class` (checked locally), so named modules for application code would sit on a stack of automatic modules. Not worth it at this size.

Visibility does not extend to Spring MVC handler classes. `InvocableHandlerMethod.doInvoke` calls `method.invoke(getBean(), args)`, and no class in `org.springframework.web` 7.0.8 calls `makeAccessible` — while the JDK refuses reflective invocation of a public method whose declaring class is package-private and outside the caller's package (`class b.Main cannot access a member of class a.Hidden with modifiers "public"`, reproduced on JDK 25). Keep `@RestController` and `@ControllerAdvice` classes public; they sit inside an internal package anyway, and nothing is gained by testing that boundary.

## Supporting packages

Technical packages beside the capabilities are normal, and both of Briefly's are legitimate: `observability` names a capability of the system, and `config` names startup translation that belongs to no single capability. What to avoid is the package named after nothing — `common`, `util`, `shared`, `helpers` — because it has no criterion for what belongs in it and therefore accumulates. `briefs.internal.domain.util` is already drifting that way: `BriefGenerationContextValidator` and `GeneratedBriefValidator` enforce News Brief invariants, which is domain policy, not utility code.

A supporting package deserves the same treatment as a capability: one public entry point, the rest hidden. The actual contract of `observability` is a single method, `HttpCallTimingInterceptor.named("GNews")`, called from `GNewsClient`.

## Applying this to Briefly

Verified against the current working tree:

- 36 of 38 top-level types are public; only `BriefMapper` and `GNewsClientException` are not.
- The import graph between top-level packages is `briefs → news` and `news → observability`, with no cycles. `briefs` reaches `news` only through `news.api.*`, and `news` reaches `observability` only through `HttpCallTimingInterceptor`. The conventions hold; nothing checks that they keep holding.
- `news.api.model.Article` and `ArticleSearchCriteria` are used directly as `briefs` domain types (`Brief` and `SourceArticle` hold them). That is coupling to the contract rather than to an implementation, which is what a contract is for; a translating layer would be premature here.
- `briefs.internal.ai.AiModelEnvironmentPostProcessor` is registered by fully-qualified name in `META-INF/spring.factories`, so moving it to `config` means editing that file too.

Ordered by value over cost, with no dependency between steps:

1. **Hide what already has no outside consumer.** Compiler-enforced, no file moves, no naming decision. `observability`: `ObservabilityFilter`, `AiCallTimingAdvisor`, `AiCallUsageAdvisor`, `ObservabilityConfig` (4 types; `HttpCallTimingInterceptor` stays public). `news.internal.gnews`: `GNews`, `GNewsArticleSearchService`, `GNewsClient`, `GNewsClientProperties`, `GNewsResponse`, `GNewsSearchParams` (6 types). `briefs.internal.ai`: `AiModelEnvironmentPostProcessor` (1 type). Two of these are exercised reflectively by more than plain instantiation — `GNewsClientProperties` through constructor binding, `GNewsClient` through `@Validated` proxying — so this step wants one smoke run of a News Brief request, not just a compile.
2. **Collapse the `briefs` domain sub-packages** into a single `domain` package. `model`, `service`, `exception`, and `util` hold ten types between them; merged, `web` still needs only `Brief`, `SourceArticle`, `BriefService`, and the two exceptions, so `GeneratedBrief`, `BriefGenerationResult`, `BriefGenerationService`, `BriefGenerationContextValidator`, and `GeneratedBriefValidator` become package-private — five more types the compiler keeps out of the web layer, and the `util` name disappears.
3. **Decide where the `news` contract lives.** Either keep `news.api.{model,service,exception}` as documentation and add a verifier when tests resume, or move the five contract types into `news` itself. Moving them makes `news` an ordinary package whose public types are its API, lets everything in `news.gnews` be package-private, and — if Modulith is adopted later — removes the need for the three `@NamedInterface` declarations the companion note describes, because the feature root is Modulith's default API package. This diverges from that note, which recommended keeping the nested `api` packages: at five contract types the extra level buys a label, while the flat root buys compiler enforcement and less future metadata.
4. **Drop the `internal` level where a capability exports nothing.** `briefs` has no contract for another capability, so `briefs.internal.web` and `briefs.web` are equally internal, and the shorter name says the same thing to Modulith's default detection. Pure naming, no enforcement change — do it last, or not at all.

Target layout if all four are taken:

```text
io.github.lefpap.briefly
├── BrieflyApplication
├── briefs                          # exports nothing; both sub-packages are internal
│   ├── domain                      # public: Brief, SourceArticle, BriefService, 2 exceptions
│   └── web                         # controllers, DTOs, mapper, handler, OpenApiConfig
├── news
│   ├── ArticleSearchService        # the contract briefs compiles against
│   ├── Article, ArticleMetadata, ArticleSearchCriteria, Publisher, ArticleSearchException
│   └── gnews                       # every type package-private
├── config
│   └── AiModelEnvironmentPostProcessor
└── observability                   # public: HttpCallTimingInterceptor
```

Nothing here requires Spring Modulith, and nothing here blocks it: the result is a valid default Modulith module model, which keeps the [companion note](spring-modulith-package-structure.md) applicable as written apart from the named-interface step that 3 removes.

## Evidence gathered locally

Read-only inspection of the working tree (type visibility, import graph, `spring.factories`, `brief.html`) and of the sources jars in `~/.m2` for Spring Framework 7.0.8 (`BeanUtils`, `SpringFactoriesLoader`, `ReflectivePropertyAccessor`, `ReflectiveMethodExecutor`, `HandlerMethod`, `InvocableHandlerMethod`), plus one throwaway two-class JDK 25 program, written outside the repository, confirming that reflective invocation of a public method on a package-private class from another package requires `setAccessible`. No tests were written or run, and no production code was compiled, because nothing changed.
