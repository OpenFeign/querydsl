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

## Releases

Regular releases (a 7.x minor from `master`, an `8.0-alphaN` from `8.x`) go through
CI: run `scripts/release.sh`, which pushes the tag, and `.github/workflows/deploy.yml`
publishes to Maven Central.

### Security advisory fixes

Fix every supported major line. Each line gets a patch release that ships only the
modules affected by the advisory, plus a BOM. This keeps the Central upload small.

1. **Branch from the latest release tag of the line**, not from a snapshot. Use the
   public maintenance branches `5.x` and `6.x`; create the branch from the tag if it
   does not exist yet. `master` and `8.x` can instead cut a regular release that
   includes the fix.
2. **Bump only the affected modules.** Give each patched module its own `<version>`
   (for example `6.12.1`) and leave the parent at the released version (`6.12`).
   Dependencies on unpatched modules keep `${project.parent.version}`. A dependency
   between two patched modules (such as `querydsl-r2dbc` on `querydsl-sql`) uses the
   new patch version.
3. **BOM:** download the BOM already published for the line from Central and change
   only its own version and the entries for the patched modules. All other entries
   keep pointing at artifacts that are already released.
4. **Build and sign locally.** Build only the patched modules with `-pl`:
   - pass `-Deasyjacoco.skip=true`;
   - on 6.x, move `.mvn/extensions.xml` aside while building and pass `-DjacocoArgLine=`;
   - on 5.x, pass `-Dsort.skip=true` and run with JDK 21.

   Then assemble the jar, sources, javadoc and pom, with `.asc`, `.md5` and `.sha1`
   files for each, into a Maven repository layout and zip it.
5. **Upload the bundle** to the Central Portal with
   `POST https://central.sonatype.com/api/v1/publisher/upload?publishingType=USER_MANAGED`.
   Poll `/api/v1/publisher/status?id=<id>` until it reports `VALIDATED`, then publish
   with `POST /api/v1/publisher/deployment/<id>`. Confirm the files are on
   `repo1.maven.org` before announcing the release.
6. **Advisory:** list one affected range per line, each with its patched version, and
   add an entry for the old `com.querydsl` coordinates with no patched version. Then
   request a CVE and publish the advisory.
