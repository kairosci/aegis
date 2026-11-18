---
name: aegis-software-engineering
description: Apply software-engineering principles to AEGIS requirements, design, implementation, verification, and delivery.
---

# AEGIS software engineering

Use this skill when planning or delivering a product change across its requirements, design, implementation, and evidence. It records practices drawn from Carlo Ghezzi, Mehdi Jazayeri, and Dino Mandrioli, *Ingegneria del software: Fondamenti e principi*, chapters 1–10.

## Work from evidence

- Identify the domain, stakeholders, and problem before choosing a solution. Turn needs into observable requirements and acceptance criteria. Keep source facts, proposals, approved decisions, and open choices distinct.
- Trace each requirement through its use cases, design elements, implementation, and verification evidence. Keep the model, code, and verification plan synchronized as each increment is delivered.
- Assess relevant product and process qualities, including correctness, reliability, robustness, performance and scalability, usability, verifiability, maintainability and evolvability, reuse, portability, comprehensibility, interoperability, productivity, timeliness, and visibility. Give each selected quality an observable evaluation method and owner.
- Choose an appropriate lifecycle and divide work into increments with dependencies, owners, risks, measures, and exit evidence. Base estimates on available evidence; do not invent dates, costs, or progress.

## Design and implement

- Separate concerns and define cohesive modules with explicit interfaces and controlled dependencies. Use abstraction and information hiding to limit the effect of change.
- Model structure and behavior at the level needed to communicate the design. Keep actor, module, data, state, multiplicity, stereotype, and failure behavior consistent across requirements and diagrams.
- Select algorithms and data structures from domain rules, expected access patterns, scale, and correctness constraints. Record material trade-offs and leave choices open when their inputs are not approved.
- Prefer increments that exercise a complete path through requirement, design, implementation, and verification. Do not present a specification or model as evidence that code exists.

## Verify and deliver

- Combine static review or analysis with dynamic verification. Choose suitable module, integration, and system checks, using both specification-based and implementation-informed cases where relevant.
- Verify functional behavior and selected quality properties. Record method, environment, inputs, results, and unresolved failures. Claim completion only for evidence actually observed.
- Evaluate tools against the engineering method and problem. Keep tool output reproducible, and do not treat automation as a substitute for design judgment or review.
- Consider public interest, safety, privacy, security, accessibility, and professional honesty throughout decisions and delivery. State residual uncertainty and risk plainly.

For RAD, SDD, template, or project-documentation edits, also apply `aegis-docs` and `aegis-antislop`.
