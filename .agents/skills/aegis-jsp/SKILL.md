---
name: aegis-jsp
description: Maintain existing legacy aegis JSP views only. New server-rendered views use Jakarta Faces with Facelets.
---

# Legacy aegis JSP views

Use only when a task explicitly requires maintenance of existing JSP pages. JSP is not the selected view technology for new aegis work. Use Jakarta Faces with Facelets instead.

- Keep request handling and business rules in controllers/use cases. JSP renders the supplied view model and does not access persistence or integrations.
- Use JSTL and Expression Language instead of Java scriptlets. Escape untrusted values for their output context. Do not emit user content as raw HTML.
- Keep user-facing text in message bundles and values in the supplied view model. Do not duplicate labels, status strings, or business rules across pages. Convert raw request values into validated typed inputs in the controller/application boundary, not in JSP. Template syntax and contract-defined form field names remain stable literals. Do not synthesize markup or names dynamically without a concrete requirement.
- Keep forms labeled and usable by keyboard and screen readers. Preserve validation messages, focus behavior, and error states in the rendered page.
- Keep JavaScript and CSS in their existing external module/resource structure. Preserve CSRF protection and authorization requirements for state-changing forms.
- Follow the repository's existing view resolver, tag library, and naming conventions rather than introducing a second rendering pattern.
