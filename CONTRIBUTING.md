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
4. Open a pull request against `master` (or `8.x` if contributing to the fluentQ 8.0 line).

## 8.x (fluentQ) Development Model

The `8.x` branch has been migrated to the new **fluentQ** naming and package namespaces (`fluentq.*`). 

* **Source of Truth**: All active codebase files are under `fluentq-libraries/` and `fluentq-tooling/`.
* **Backward Compatibility**: The legacy packages under `querydsl/` are automatically **generated** from the new `fluentq` code. Do **NOT** edit files in `querydsl/` directly as they will be overwritten.
* **Compatibility Pack Generation**: If you make changes in the `fluentq-` directories, regenerate the legacy wrappers using:
  ```bash
  python3 scripts/generate-compat-wrappers.py
  ```

### Merging / Cherry-Picking from Master (Upstream)

Because package namespaces and file directories have changed on `8.x`, standard git merges or cherry-picks from `master` will conflict or fail to target the correct directories.

To apply a commit or patch file from `master` to `8.x` with automatic translation:
```bash
python3 scripts/apply-upstream-patch.py <commit-hash-or-patch-file>
```
This script will:
1. Fetch the diff from the specified commit hash or patch file.
2. Translate all file paths (`querydsl-libraries` -> `fluentq-libraries`, etc.) and package namespaces (`com.querydsl` -> `fluentq`, etc.).
3. Apply the translated diff to your current working directory.
4. Automatically run `generate-compat-wrappers.py` to keep the compatibility pack up to date.

