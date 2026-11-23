# AGENTS.md

Guidance for coding agents working in this repository. Build commands and the
module layout are in [`CLAUDE.md`](CLAUDE.md); the 8.x branch and how it diverges
from `master` are described in [`docs/agents.md`](docs/agents.md).

## Branches

- **`master` is the 7.x line.** It stays backwards compatible: additive changes and
  bug fixes only. japicmp checks every module against the last 7.x release
  (`japicmp.baseline` in the root `pom.xml`) and fails the build on any binary or
  source incompatibility.
- **`8.x` is the next major (fluentQ).** It targets Jakarta Persistence 4.0,
  EclipseLink 6 and Hibernate 8; those CI jobs stay optional until each reaches GA.

Target `8.x`, not `master`, for:

- **JPA 4 work**: Jakarta Persistence 4.0, Hibernate 8, EclipseLink 6.
- **Any breaking change**: binary or source API breaks, removals, changed default
  behaviour, raising the minimum Java/JPA/Hibernate baseline, or anything japicmp
  flags against the last 7.x release.

`8.x` has a different source layout (`fluentq-*` modules, `fluentq.*` packages), so
PRs for it must be written against that layout, not ported from a `master` diff.
`master` is synced into `8.x` by porting each change into fluentq and then
`git merge -s ours`, never a plain merge; see `.agents/AGENTS.md` on `8.x` and
[`docs/agents.md`](docs/agents.md).
