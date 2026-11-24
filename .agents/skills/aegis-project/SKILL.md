---
name: aegis-project
description: Plan and implement changes that cross aegis monorepo modules, build configuration, CI, or repository structure.
---

# aegis project work

Use this skill for cross-module planning, repository layout, dependency changes, build and CI work, and project-level decisions.

- Inspect the current tree and existing conventions before choosing a location or adding a tool. The repository is a monorepo. Do not create placeholder modules or duplicate an existing capability.
- Keep project documentation and all source templates and template assets under `docs/`. The reference template is `docs/template/template.docx`. Generated DOCX files go to `build/` or CI artifacts, not `docs/`.
- Keep project data and configuration under the root `config/` tree. Keep application source under `src/`.
- Make the smallest change that completes the requested work. Update the Makefile, workflow, README, or affected module boundaries only when the change requires it.
- Keep confirmed facts, decisions, and unresolved choices distinct. Mark unvalidated values as pending. Do not invent providers, deployment targets, SLAs, or implemented features.
- Avoid magic values and duplicated sources of truth across every module. Parse external input once at its boundary into validated, typed values. Derive display and operational values from canonical state. Put deployment-specific values in typed configuration and user-facing text in localization resources. Keep protocol-required literals (for example schema names and wire tokens) named, centralized where practical, and traceable to their contract. Do not obscure them with runtime string construction or needless abstraction.
- Before finishing, check changed paths, references, ignore rules, and the relevant build or workflow. Do not rewrite Git history or publish/deploy without explicit authorization.
