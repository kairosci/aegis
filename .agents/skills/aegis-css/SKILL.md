---
name: aegis-css
description: Create or revise AEGIS styles for responsive layouts, accessible controls, maps, forms, and results.
---

# AEGIS CSS

Use when changing CSS, layout, component states, or responsive behavior.

- Follow existing design tokens and component conventions. Add a token only when it represents a reused design decision, not for a one-off value.
- Avoid unexplained magic numbers and duplicated style values. Use design tokens for shared colors, spacing, typography, and interaction dimensions. Derive responsive sizing with intrinsic layout and relative units when appropriate. Keep one-off values local when promoting them would add indirection, and do not calculate static design values at runtime.
- Prefer responsive layouts, intrinsic sizing, and logical properties. Avoid fixed heights that clip translated, zoomed, or assistive-technology content.
- Preserve visible keyboard focus, sufficient contrast, and distinct hover, focus, selected, disabled, and error states. Do not communicate status by color alone.
- Respect `prefers-reduced-motion`. Do not add nonessential motion to map or route updates.
- Scope selectors to components and avoid `!important` unless an existing integration requires it and the reason is documented.
