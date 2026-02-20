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
