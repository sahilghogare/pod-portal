# Branch Policy

## Long-lived branches
| Branch | Purpose | Rules |
|---|---|---|
| `main` | Stable, release-ready code. Every release is tagged here. | No direct commits. Updated only by merging `develop` through a pull request. |
| `develop` | Integration branch for finished features. | No direct commits. Updated only by merging pull requests from short-lived branches. |

## Short-lived branches
Create them from `develop` and delete them after merging.

| Prefix | Use | Example |
|---|---|---|
| `feature/` | New functionality | `feature/US-02-create-delivery` |
| `bugfix/` | Fix found during development | `bugfix/12-search-empty-result` |
| `docs/` | Documentation only | `docs/update-readme` |
| `chore/` | Build, config or tooling | `chore/add-jenkinsfile` |
| `hotfix/` | Urgent fix on a release, branched from `main` | `hotfix/1.0.1-login-error` |

## Naming rules
- All lowercase, words separated by hyphens.
- Start with the prefix, then the backlog ID or issue number, then a short description.
- Maximum of about 40 characters after the prefix.

## Commit message convention
Format: `type: short summary in present tense` (under 72 characters).

| Type | Meaning |
|---|---|
| feat | New feature |
| fix | Bug fix |
| docs | Documentation |
| test | Adding or changing tests |
| build | Build tool or dependency changes |
| ci | Jenkins or pipeline changes |
| chore | Maintenance, configuration |
| refactor | Code change with no behaviour change |

Examples: `feat: add delivery creation form`, `fix: reject duplicate tracking numbers`.

## Pull request rules
1. Open a pull request from the short-lived branch into `develop`.
2. Link the issue (for example `Closes #5`).
3. The project must build (`mvn clean package`) and tests must pass.
4. Review the diff and leave at least one review comment or approval note before merging.
5. Merge with a merge commit, then delete the branch.

## Releases
When `develop` is stable, open a pull request from `develop` into `main`, merge it and tag the release,
for example `v0.1.0`.
