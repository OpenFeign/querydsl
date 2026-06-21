# AGENTS.md

This file provides guidance to AI coding assistants when working with the **8.x (fluentQ)** codebase in this repository.

## Overview of the 8.x Branch

In the 8.x release, the project has been renamed from **Querydsl** to **fluentQ**, utilizing the `fluentq.*` package prefix.

* **fluentq-libraries/** and **fluentq-tooling/**: These contain the active codebase. They use the `fluentq` package structure and must be the sole target of code edits and updates.
* **querydsl/**: Contains the legacy backwards compatibility wrappers (`com.querydsl.*` package structure). These files are **automatically generated** from the fluentq source code. Do **NOT** edit files in `querydsl/` directly.

## Upstream Merge and Patch Workflow

Because file paths and package namespaces differ between the upstream `master` branch and the `8.x` branch, direct git merges or cherry-picks will conflict or target the wrong directories.

To apply a commit or a `.patch`/`.diff` file from `master` to `8.x`, run:
```bash
python3 scripts/apply-upstream-patch.py <commit-hash-or-patch-file>
```

This helper script will:
1. Extract the git diff from the specified commit hash or file.
2. Automatically rewrite file paths (e.g. `querydsl-libraries` -> `fluentq-libraries`).
3. Automatically translate namespaces (e.g. `com.querydsl` -> `fluentq`).
4. Apply the translated diff to your current working tree.
5. Automatically run the wrapper generator script `scripts/generate-compat-wrappers.py` to regenerate the backwards-compatibility layer.

## Build and Verification Commands

### Essential Build Commands
- `./mvnw clean install` - Build the entire project.
- `./mvnw clean install -Pquickbuild` - Skip tests and validation for faster builds.
- `./mvnw -Pdev verify` - Run tests locally with the development profile (excludes external databases).
- `./mvnw -Pexamples clean install` - Verify and build the canary examples to ensure legacy compatibility is intact.

### Compatibility Pack Generation
If you manually change any code in the `fluentq-` directories, always regenerate the compatibility wrappers by running:
```bash
python3 scripts/generate-compat-wrappers.py
```
