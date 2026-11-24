---
name: aegis-xml
description: Edit or validate XML used by aegis, including Jakarta EE, Maven, OpenAPI, and document-related configuration.
---

# aegis XML

Use when changing XML source or configuration files.

- Inspect the existing namespace declarations, schema location, encoding, and line endings before editing. Preserve them unless the requested change requires otherwise.
- Make a focused structural edit. Do not rewrite or pretty-print an entire file when a localized change is sufficient.
- Keep XML well-formed and validate against the declared XSD or the owning tool when available. A parser-only check proves well-formedness, not schema validity.
- Parse XML with a namespace-aware parser and validate against its declared schema where applicable. Do not extract structured values with ad hoc string splitting or regular expressions. Keep environment-specific values in externalized configuration and avoid duplicated literals. XML syntax, namespace URIs, schema identifiers, and contract-defined element/attribute names are fixed protocol data: preserve and name them as the format requires rather than attempting runtime computation.
- For XML derived from user input, disable external entity resolution and external DTD loading. Do not introduce external entities.
- Preserve comments, processing instructions, extension elements, and namespace-qualified names. Do not infer that unknown elements are safe to remove.
