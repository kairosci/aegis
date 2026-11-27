---
name: aegis-docs
description: Create or revise AEGIS RAD, SDD, template, and project documentation while preserving the supplied academic template and traceable requirements.
---

# AEGIS documentation

Use for changes to `docs/RAD.md`, `docs/SDD.md`, template files under `docs/template/`, or related project documentation.

Apply the companion `aegis-antislop` skill to every documentation change. Use it to review evidence, traceability, wording, actors, failure paths, diagrams, and generated images.

- Follow the structure of the specific academic template or example documents supplied for the current task. When the user identifies example PDFs as the structural reference, mirror their section hierarchy and numbering in RAD/SDD. Preserve the example's headings and section order while adapting domain content without copying its project-specific claims.
- Keep functional and non-functional requirements in tables. State an explicit actor/system action for functional requirements. Give each non-functional requirement an action and an observable criterion. Include performance where relevant.
- Give use cases actors, preconditions, and postconditions. Preserve uncertainty as an explicit TBD rather than presenting an assumption as a fact.
- Write concise Italian prose suitable for an academic project. Remove filler, promotional claims, repeated context, and invented implementation status. Preserve technical terms where they are precise.
- Prefer Markdown prose and tables. Avoid decorative symbols and lists unless they improve structure or the template requires them. For every diagram or image, provide a concise text description of what it represents. Do not put decorative numbering in images. Keep diagrams in Markdown-friendly formats when possible.
- Do not invent numeric thresholds, freshness periods, service levels, providers, or policy values. Identify the source and owner of each stated value. Mark unresolved values as TBD. Distinguish source facts from calculated results and document the rule and inputs for derived values. Use stable names for requirements and domain concepts rather than duplicating subtly different labels.
- When a workflow spans the database and object storage or another external system, document the commit boundary, private or pending state, and retry or cleanup behavior. Do not describe those operations as one atomic transaction unless the selected systems provide that guarantee.
- Keep Markdown files in `docs/`. Link the source DOCX and extracted assets at their actual paths. The template is a reference artifact, not an export target.
- Generate diagram images with `make images` when RAD, SDD, or Mermaid tooling changes. Review the generated images manually. Do not add a DOCX generation workflow.
