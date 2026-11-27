# Le versioni degli strumenti sono dichiarate in mise.toml.

MISE ?= mise
TOOL := $(MISE) exec --
MERMAID_BIN ?= config/documentation/node_modules/.bin/mmdc
PUPPETEER_BIN ?= config/documentation/node_modules/.bin/puppeteer
MERMAID ?= $(TOOL) $(MERMAID_BIN)
PUPPETEER ?= $(TOOL) $(PUPPETEER_BIN)
PUPPETEER_CONFIG ?=
IMAGE_DIR ?= docs/images
IMAGE_SIZE ?= 1600
IMAGE_SCALE ?= 2
WORD_DIAGRAM_CONFIG ?= config/documentation/mermaid-word.json

.DEFAULT_GOAL := help
.PHONY: help install-tools install-image-tools check-node check-mermaid images

help:
	@printf '%s\n' \
	  'make install-tools       Install the pinned toolchain from mise.toml' \
	  'make install-image-tools Install Mermaid CLI and Chromium headless' \
	  'make images              Generate RAD and SDD diagram images in $(IMAGE_DIR)'

install-tools:
	$(MISE) install

install-image-tools: install-tools
	$(TOOL) npm ci --prefix config/documentation
	$(TOOL) npm exec --prefix config/documentation -- puppeteer browsers install chrome-headless-shell

check-node: install-tools
	@actual="$$($(TOOL) node --version | sed 's/^v//')"; \
	if [ -z "$$actual" ]; then \
	  printf 'Node.js non disponibile tramite mise.\n' >&2; \
	  exit 1; \
	fi

check-mermaid: check-node
	@expected="$$($(TOOL) node -p "require('./config/documentation/package.json').devDependencies['@mermaid-js/mermaid-cli']")"; \
	if [ ! -x "$(MERMAID_BIN)" ]; then \
	  printf 'Mermaid CLI non trovato. Eseguire make install-image-tools.\n' >&2; \
	  exit 1; \
	fi; \
	actual="$$($(MERMAID) --version)"; \
	if [ "$$actual" != "$$expected" ]; then \
	  printf 'Versione Mermaid CLI richiesta: %s. Versione trovata: %s.\n' "$$expected" "$$actual" >&2; \
	  exit 1; \
	fi; \
	browsers="$$($(PUPPETEER) browsers list)"; \
	if ! printf '%s\n' "$$browsers" | grep -q 'chrome-headless-shell@'; then \
	  printf 'Chromium headless non trovato. Eseguire make install-image-tools.\n' >&2; \
	  exit 1; \
	fi

images: check-mermaid
	@temporary_dir="$$(mktemp -d)"; \
	trap '$(TOOL) node -e "require(\"node:fs\").rmSync(process.argv[1], {recursive: true, force: true})" "$$temporary_dir"' EXIT; \
	mkdir -p "$$temporary_dir/rad" "$$temporary_dir/sdd"; \
	$(MERMAID) --configFile "$(WORD_DIAGRAM_CONFIG)" $(if $(PUPPETEER_CONFIG),--puppeteerConfigFile "$(PUPPETEER_CONFIG)") --input docs/RAD.md --output "$$temporary_dir/rad-rendered.md" --outputFormat png --artefacts "$$temporary_dir/rad" --size "$(IMAGE_SIZE)" --scale "$(IMAGE_SCALE)" --jobs 1 && \
	$(MERMAID) --configFile "$(WORD_DIAGRAM_CONFIG)" $(if $(PUPPETEER_CONFIG),--puppeteerConfigFile "$(PUPPETEER_CONFIG)") --input docs/SDD.md --output "$$temporary_dir/sdd-rendered.md" --outputFormat png --artefacts "$$temporary_dir/sdd" --size "$(IMAGE_SIZE)" --scale "$(IMAGE_SCALE)" --jobs 1 && \
	$(TOOL) node config/documentation/render-word-images.mjs "$$temporary_dir/rad" "$$temporary_dir/sdd" "$(IMAGE_DIR)"
