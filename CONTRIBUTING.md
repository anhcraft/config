# Contribution
- Read [docs/Internal.md](./docs/Internal.md) to know the internal working.

## Pull request
- For PR, ensure there is associated issue(s) created, if not, please make one
- To avoid multiple individuals working on the same issue: Comment under the issue to be assigned.
- Feature PR must include:
  - Successfully-tested code
  - JUnit tests if needed
  - Changes to the Internal.md if needed
  - Proposal changes to the wiki if needed
- Convention: [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

## Commit message guidelines
- Adapted from: https://github.com/angular/angular/blob/main/CONTRIBUTING.md#commit

```
<type>(<scope>): <short summary>
  │       │             │
  │       │             └─⫸ Summary in present tense. Not capitalized. No period at the end.
  │       │
  │       └─⫸ Commit Scope: core|configdoc|example|benchmark
  │
  └─⫸ Commit Type: build|ci|docs|feat|fix|refactor|test
```
- `build`: change to build files, dependencies
- `ci`: change GitHub workflow, other CI services, etc
- `docs`: update `docs` files
- `feat`: a new feature (corresponds to a minor version)
- `fix`: a new fix (corresponds to a patch version)
- `refactor`: refactor code (spotless), optimize code
- `test`: changes related to test
