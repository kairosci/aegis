# Tutte le versioni degli strumenti sono in mise.toml.
# Ogni comando passa da `mise exec`, quindi il Makefile non ripete versioni.

MISE ?= mise
TOOL := $(MISE) exec --
PANDOC ?= $(TOOL) pandoc
MERMAID_BIN ?= config/documentation/node_modules/.bin/mmdc
PUPPETEER_BIN ?= config/documentation/node_modules/.bin/puppeteer
MERMAID ?= $(TOOL) $(MERMAID_BIN)
PUPPETEER ?= $(TOOL) $(PUPPETEER_BIN)
PUPPETEER_CONFIG ?=
OUTPUT_DIR ?= build/docs
DOCX_TOOL_INPUTS = Makefile mise.toml config/documentation/package.json config/documentation/package-lock.json $(PUPPETEER_CONFIG)

.DEFAULT_GOAL := help
.PHONY: help install-tools install-doc-tools check-node check-pandoc check-mermaid compile package javadoc render-docs docs

help:
	@printf '%s\n' \
	  'make install-tools  Install the pinned toolchain from mise.toml' \
	  'make compile        Compile the Java reactor with the pinned JDK and Maven' \
	  'make package        Build the Java artifacts without running tests' \
	  'make javadoc        Generate Java API docs and check Javadoc completeness' \
	  'make docs           Generate RAD and SDD DOCX files in $(OUTPUT_DIR)' \
	  'make install-doc-tools  Install pinned Node tools and Chromium for Mermaid exports' \
	  'Set PANDOC and MERMAID to select alternate executables'

install-tools:
	$(MISE) install

install-doc-tools: install-tools
	$(TOOL) npm ci --prefix config/documentation
	$(TOOL) npm exec --prefix config/documentation -- puppeteer browsers install chrome-headless-shell

check-node: install-tools
	@actual="$$($(TOOL) node --version | sed 's/^v//')"; \
	if [ -z "$$actual" ]; then \
	  printf 'Node.js non disponibile tramite mise.\n' >&2; \
	  exit 1; \
	fi

check-pandoc: install-tools
	@version="$$($(PANDOC) --version | sed -n '1s/^pandoc //p')"; \
	if [ -z "$$version" ]; then \
	  printf 'Pandoc non disponibile tramite mise.\n' >&2; \
	  exit 1; \
	fi

check-mermaid: check-node
	@expected="$$($(TOOL) node -p "require('./config/documentation/package.json').devDependencies['@mermaid-js/mermaid-cli']")"; \
	if [ ! -x "$(MERMAID_BIN)" ]; then \
	  printf 'Mermaid CLI non trovato. Eseguire make install-doc-tools.\n' >&2; \
	  exit 1; \
	fi; \
	actual="$$($(MERMAID) --version)"; \
	if [ "$$actual" != "$$expected" ]; then \
	  printf 'Versione Mermaid CLI richiesta: %s. Versione trovata: %s.\n' "$$expected" "$$actual" >&2; \
	  exit 1; \
	fi; \
	browsers="$$($(PUPPETEER) browsers list)"; \
	if ! printf '%s\n' "$$browsers" | grep -q 'chrome-headless-shell@'; then \
	  printf 'Chromium headless non trovato. Eseguire make install-doc-tools.\n' >&2; \
	  exit 1; \
	fi

compile: install-tools
	$(TOOL) mvn -B -f pom.xml compile

package: install-tools
	$(TOOL) mvn -B -f pom.xml package

javadoc: install-tools
	$(TOOL) mvn -B -f pom.xml install
	$(TOOL) mvn -B -f pom.xml javadoc:javadoc -Ddoclint=all -Dshow=private

render-docs: check-mermaid
	mkdir -p "$(OUTPUT_DIR)"
	$(MERMAID) $(if $(PUPPETEER_CONFIG),--puppeteerConfigFile "$(PUPPETEER_CONFIG)") --input docs/RAD.md --output "$(OUTPUT_DIR)/rad-rendered.md" --outputFormat png --artefacts "$(OUTPUT_DIR)" --size 630 --jobs 1
	$(MERMAID) $(if $(PUPPETEER_CONFIG),--puppeteerConfigFile "$(PUPPETEER_CONFIG)") --input docs/SDD.md --output "$(OUTPUT_DIR)/sdd-rendered.md" --outputFormat png --artefacts "$(OUTPUT_DIR)" --size 630 --jobs 1

docs: check-node check-pandoc
	$(MAKE) --no-print-directory $(OUTPUT_DIR)/rad-is.docx $(OUTPUT_DIR)/sdd-is.docx

$(OUTPUT_DIR)/rad-is.docx: docs/RAD.md docs/template/template.docx $(DOCX_TOOL_INPUTS) | render-docs
	$(PANDOC) --from=gfm --to=docx --standalone --reference-doc=docs/template/template.docx --resource-path=".:docs:$(OUTPUT_DIR)" "$(OUTPUT_DIR)/rad-rendered.md" --output="$@"

$(OUTPUT_DIR)/sdd-is.docx: docs/SDD.md docs/template/template.docx $(DOCX_TOOL_INPUTS) | render-docs
	$(PANDOC) --from=gfm --to=docx --standalone --reference-doc=docs/template/template.docx --resource-path=".:$(OUTPUT_DIR)" "$(OUTPUT_DIR)/sdd-rendered.md" --output="$@"
