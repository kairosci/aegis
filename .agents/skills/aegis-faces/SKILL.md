---
name: aegis-faces
description: Build or revise AEGIS Jakarta Faces views using Facelets and accessible server-rendered UI patterns.
---

# AEGIS Jakarta Faces and Facelets

Use for the selected server-rendered view layer. Keep Faces as an adapter around application use cases. Do not put business rules, persistence, or external integrations in view beans or Facelets pages.

- Follow the Jakarta Faces and Jakarta EE versions and view conventions selected by the repository. Use Facelets XHTML and the project's configured component/tag libraries. Do not add a second rendering framework.
- Keep view models focused on presentation and convert user input into validated application inputs before invoking use cases. Treat submitted values as untrusted and apply server-side validation.
- Keep user-facing text in message bundles, escape values by default, and expose validation and operation status accessibly. Preserve keyboard use, labels, focus behavior, and screen-reader announcements.
- Keep JavaScript and CSS in their external modules/resources. Do not put Java scriptlets or business logic in Facelets. Keep state-changing operations protected by the configured authentication, authorization, and CSRF controls.
- Use stable, contract-defined component and field identifiers where needed. Avoid duplicated labels and magic values. Parse boundary input into typed values and derive view state from canonical application results.
- Preserve XML namespaces and validate Facelets XML against the owning runtime/tool where available. Do not generate markup by concatenating untrusted strings.
