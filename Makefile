.PHONY: book lint-book test check clean pack hooks

# Ordered glob: directories and files are named NN-slug so lexical sort
# equals reading order.
CHAPTERS := $(sort $(wildcard manuscript/00-frontmatter/*.md) \
                    $(wildcard manuscript/10-part-1-the-problem/*.md) \
                    $(wildcard manuscript/20-part-2-the-method/*.md) \
                    $(wildcard manuscript/30-part-3-the-structure/*.md) \
                    $(wildcard manuscript/40-part-4-reference/*.md) \
                    $(wildcard manuscript/90-backmatter/*.md))

# Figures (manuscript/figures/*.svg) are referenced from chapters by
# repo-root-relative path; --resource-path=. resolves them from here and
# --embed-resources (pandoc >= 2.19) inlines them so build/book.html is
# self-contained. See manuscript/figures/README.md.
book: build/book.md
	@if command -v pandoc >/dev/null 2>&1; then \
		echo "pandoc found: rendering build/book.html"; \
		pandoc -s -f markdown -t html5 -o build/book.html build/book.md \
				--citeproc --bibliography=manuscript/bibliography.bib \
				--resource-path=. --embed-resources \
				--metadata title="EHR Testing Guide"; \
	else \
		echo "NOTICE: pandoc not found on PATH; skipping build/book.html"; \
		echo "        (build/book.md was still produced)"; \
	fi

# Catches build-output regressions that only showed up by manually grepping
# build/book.html once (the citeproc-wiring gap) -- make that check
# permanent. Grep patterns are newline-flattened first: the manuscript's
# ~75-col hard wrap splits phrases across lines.
lint-book: build/book.md
	@FAIL=0; \
	if perl -0777 -pe 's/<!--.*?-->//gs' build/book.md | tr '\n' ' ' | grep -q 'TODO'; then \
		echo "lint-book: FAIL -- literal TODO found in build/book.md outside HTML comments"; \
		FAIL=1; \
	fi; \
	cited=$$(grep -rhoE '\[@[A-Za-z0-9_;@ -]+\]' manuscript --include='*.md' | tr ' ;' '\n\n' | grep -oE '@[A-Za-z0-9_-]+' | sed 's/^@//' | sort -u); \
	for key in $$cited; do \
		if sed -n "/^@[a-z]*{$$key,/,/^}/p" manuscript/bibliography.bib | grep -q TODO; then \
			echo "lint-book: FAIL -- cited entry '$$key' still carries a TODO in bibliography.bib"; \
			FAIL=1; \
		fi; \
	done; \
	if [ -f build/book.html ]; then \
		if sed -n '/<div id="refs"/,$$p' build/book.html | tr '\n' ' ' | grep -qE 'n\.d\.|TODO'; then \
			echo "lint-book: FAIL -- literal n.d. or TODO found in the refs section of build/book.html"; \
			FAIL=1; \
		fi; \
		if tr '\n' ' ' < build/book.html | grep -qE '<strong>[A-Za-z0-9_]+\?</strong>'; then \
			echo "lint-book: FAIL -- unresolved [@key] citation found in build/book.html"; \
			FAIL=1; \
		fi; \
	fi; \
	if [ "$$FAIL" = "1" ]; then exit 1; fi; \
	echo "lint-book: OK"

build/book.md: $(CHAPTERS)
	@mkdir -p build
	@rm -f build/book.md
	@for f in $(CHAPTERS); do \
		cat "$$f" >> build/book.md; \
		printf '\n\n' >> build/book.md; \
	done
	@echo "wrote build/book.md ($(words $(CHAPTERS)) chapters)"

test:
	cd companion && clojure -X:test

check: book lint-book test

clean:
	rm -rf build

# Concatenates every source text file in the repo into one file outside the
# repo, for pasting into a chat UI that can't read the filesystem directly.
# Excludes dotfiles/dotdirs generally (.git, .lsp, .clj-kondo, .cpcache) and
# the generated build/ directory -- only hand-written sources go in -- but
# then re-includes an explicit allow-list of dotfiles/dotdirs that a fresh
# chat session actually needs for continuity and enforcement: .agents/
# (plans, skills, handoffs, reviews), .githooks/ (the WSL-only pre-commit
# hook), .gitattributes (the LF-on-checkout enforcement it backs up), and
# .gitignore (so a session can see what's deliberately excluded).
# Without this second pass those files were invisible to chat sessions --
# notably, a chat session could never confirm the WSL hook even exists.
#
# Uses the shell's own $HOME (via $(shell echo $$HOME)), not Make's built-in
# $(HOME): on native Windows Make, $(HOME) resolves to a backslash path
# (e.g. C:\Users\you) that breaks shell redirection in the recipe below,
# while the invoking shell's $HOME is already the correct POSIX-style path
# on both Windows (Git Bash/MSYS) and WSL.
PACK_OUTPUT := $(shell echo $$HOME)/ehr-testing-guide-pack.txt
PACK_PATTERNS := *.md *.clj *.edn *.bib Makefile LICENSE *.cff
PACK_DOTFILE_ALLOWLIST := ./.agents ./.githooks ./.gitattributes ./.gitignore

PACK_FIND_ARGS := -name "$(firstword $(PACK_PATTERNS))"
PACK_FIND_ARGS += $(foreach p,$(wordlist 2,$(words $(PACK_PATTERNS)),$(PACK_PATTERNS)),-o -name "$(p)")

pack:
	@echo "Creating $(PACK_OUTPUT)..."
	@echo "Including patterns: $(PACK_PATTERNS)"
	@find . -type f \( $(PACK_FIND_ARGS) \) ! -path "*/.*" ! -path "*/build/*" | sort | while read -r file; do \
		echo "========== FILE: $$file =========="; \
		cat "$$file"; \
		echo ""; \
		echo "========== END FILE =========="; \
		echo ""; \
	done > $(PACK_OUTPUT)
	@echo "Including dotfile allow-list: $(PACK_DOTFILE_ALLOWLIST)"
	@for d in $(PACK_DOTFILE_ALLOWLIST); do find "$$d" -type f 2>/dev/null; done | sort | while read -r file; do \
		echo "========== FILE: $$file =========="; \
		cat "$$file"; \
		echo ""; \
		echo "========== END FILE =========="; \
		echo ""; \
	done >> $(PACK_OUTPUT)
	@echo "" >> $(PACK_OUTPUT)
	@echo "========== DIRECTORY LISTING: ls -alR ===========" >> $(PACK_OUTPUT)
	@ls -alR . >> $(PACK_OUTPUT)
	@echo "" >> $(PACK_OUTPUT)
	@echo "========== END DIRECTORY LISTING ===========" >> $(PACK_OUTPUT)
	@echo "Done! Created $(PACK_OUTPUT)"

# Installs this repo's tracked git hooks (see .githooks/) by pointing git
# at that directory instead of the untracked, per-clone .git/hooks/. Run
# once per clone/worktree. See AUTHORS-GUIDE.md "Git operations: WSL only"
# for what the pre-commit hook enforces and why.
hooks:
	git config core.hooksPath .githooks
	@echo "core.hooksPath set to .githooks -- pre-commit WSL check is now active."
