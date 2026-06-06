# Contributing to Querydsl

GitHub pull requests are the way to contribute to Querydsl.

## Documentation

The documentation site is built with [Jekyll](https://jekyllrb.com/) using the
[just-the-docs](https://just-the-docs.com/) theme and hosted on GitHub Pages.

Source files live in the `docs/` directory. Changes to `docs/**` on the
`master` branch automatically trigger a rebuild and deploy.

### Editing Documentation

1. Fork the repository and create a branch from `master`.
2. Edit or add Markdown files under `docs/`.
3. Use Liquid variables for version and groupId:
   - `{{ site.querydsl_version }}` — current release version
   - `{{ site.group_id }}` — Maven groupId
4. Open a pull request.

### Local Preview

To preview the site locally:

```bash
cd docs
bundle install
bundle exec jekyll serve
```

Then open `http://localhost:4000/querydsl/` in your browser.

### Structure

```
docs/
├── _config.yml           # Jekyll configuration and variables
├── Gemfile               # Ruby dependencies
├── index.md              # Landing page
├── introduction.md       # Background and principles
├── tutorials/            # Backend-specific tutorials
├── guides/               # Cross-cutting guides
├── troubleshooting.md    # Common issues
└── migration.md          # Migration from upstream querydsl
```

## Code Contributions

### Building

```bash
./mvnw -Pquickbuild clean install
```

### Running Tests

```bash
# Without external databases
./mvnw -Pdev verify

# CI profile
./mvnw -Pci verify
```

### Code Formatting

```bash
./mvnw -Pdev initialize
```

### Pull Request Process

1. Fork the repository and create a feature branch.
2. Make your changes and add tests where appropriate.
3. Run `./mvnw -Pdev verify` to make sure tests pass.
4. Open a pull request against `master`.

## Releasing

Releases are cut with the `scripts/release.sh` helper. **Never create a release
tag by hand** — manual tagging is how a release ends up pointing at a
`-SNAPSHOT` pom, which CircleCI happily builds while `central-publishing`
silently skips the upload (a green build that publishes nothing to Maven
Central).

### Prerequisites

- A clean working tree on the branch you are releasing (usually `master`).
- The pom version is the `-SNAPSHOT` you intend to release. The release version
  is derived by stripping `-SNAPSHOT`, so set it deliberately beforehand:
  - `7.3-SNAPSHOT` → releases `7.3`
  - `7.3.0-SNAPSHOT` → releases `7.3.0`

### Cut the release

```bash
./scripts/release.sh
```

The script:

1. Reads `project.version` and strips `-SNAPSHOT` to get the release version.
2. Runs `versions:set -DremoveSnapshot` across all modules and commits
   `prepare release <version>`, then tags that commit.
3. Bumps the poms to the next development iteration (`<next>-SNAPSHOT`) and
   commits `[ci skip] updating versions to next development iteration`.

Because the version bump happens *before* the tag, the tag always points at a
non-SNAPSHOT pom.

### Push

The script keeps the changes local (`-DpushChanges=false`). Review, then push —
**pushing the tag is what triggers the release**:

```bash
git log --oneline -3      # confirm the two commits and the new tag
git push origin master    # the prepare-release + next-snapshot commits
git push origin <tag>      # e.g. git push origin 7.3 — triggers deployRelease on CircleCI
```

### Verify it actually published

A green CircleCI job is not proof — confirm all three:

```bash
# 1. The tagged commit must NOT be a snapshot
git show <tag>:pom.xml | grep -m1 '<version>'   # must not contain -SNAPSHOT

# 2. Watch the release workflow to green
#    https://app.circleci.com/pipelines/gh/OpenFeign/querydsl

# 3. Confirm artifacts landed (allow a few minutes for indexing)
#    https://central.sonatype.com/artifact/io.github.openfeign.querydsl/querydsl-core
```
