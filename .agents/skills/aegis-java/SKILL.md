---
name: aegis-java
description: Design or implement AEGIS Java and Jakarta EE code, including domain rules, use cases, REST adapters, persistence, and integrations.
---

# AEGIS Java and Jakarta EE backend

Use when adding or changing Java code for the AEGIS backend or workers.

- Follow the Java LTS version and Jakarta EE platform version configured by the repository. Do not upgrade either as a side effect of a feature.
- Use Jakarta REST for HTTP resource adapters and the platform's dependency injection and persistence APIs where selected. Keep web resources thin. Do not couple domain or application rules to Jakarta EE APIs.
- Organize code by domain (`places`, `accessibility`, `routing`, `reports`, `identity`, `integrations`) and keep domain, application, and adapter responsibilities distinct.
- Put invariants in domain objects. Prefer immutable value objects for identifiers, coordinates, source freshness, and validated inputs. Keep HTTP and persistence DTOs separate from domain types.
- Parse and validate untrusted strings, numbers, dates, coordinates, and enum tokens at adapter boundaries. Pass domain types inward and serialize them explicitly at boundaries. Represent domain states with enums or sealed types, not scattered string comparisons. Derive values from canonical state instead of duplicating them. Use named constants for genuine fixed domain rules and typed configuration for environment-dependent values. Never use silent fallback values that turn missing or stale accessibility data into accessible.
- Keep user-facing messages in message resources. Isolate fixed protocol, SQL, and framework tokens according to their contracts. Do not dynamically assemble them to claim they are computed, and do not add abstraction for a single literal without a reuse or change-control need.
- Define application ports for repositories and external providers. Implement them in adapters. Keep controllers thin and avoid god services or business rules in controllers.
- Put transactions around use cases that require atomic state changes. Keep slow network calls outside database transactions where possible. Make retries and external effects explicit.
- Use constructor injection, explicit error handling, and names that state domain intent. Add or update tests for changed rules and transitions when the repository provides a test setup.
