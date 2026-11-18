---
name: aegis-javascript
description: Implement AEGIS browser behavior, form interactions, and accessible progressive enhancement in JavaScript.
---

# AEGIS JavaScript

Use when changing browser-side behavior or JavaScript modules.

- Follow the runtime and module setup already present. Do not add a framework, bundler, or dependency unless the task needs it and the repository's architecture supports it.
- Keep DOM behavior modular and tied to semantic controls. Preserve keyboard operation, visible focus, screen-reader status announcements, and usable error states.
- Treat browser input and server responses as untrusted. Use safe DOM sinks such as `textContent`. Avoid building HTML from interpolated values.
- Parse external data against an explicit schema at the boundary, validate before use, and keep domain states as closed sets or discriminated types rather than free-form strings. Keep user-facing text in localization resources and configuration outside source code. Derive UI state from canonical data instead of parallel flags. Name and centralize unavoidable contract literals (such as API field names). Do not replace them with brittle runtime string construction.
- Handle loading, empty, success, and failure states explicitly. Do not make client-side validation a substitute for server validation.
- Keep presentation in CSS and domain decisions on the server. Avoid inline handlers and global mutable state when the existing structure permits modules.
