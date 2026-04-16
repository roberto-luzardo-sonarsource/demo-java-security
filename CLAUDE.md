# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose

This is a **demo/training project** showcasing Java security vulnerabilities for SonarQube analysis and security training. The code intentionally contains security flaws (SQL injection, XSS, insecure deserialization, weak crypto, hardcoded credentials, etc.) — do not "fix" vulnerabilities unless explicitly asked to do so.

## Build & Test Commands

```bash
# Build and run all tests
mvn clean verify

# Run a single test class
mvn test -Dtest=WebUtilsTest

# Run SonarQube analysis (requires SONAR_TOKEN and SONAR_HOST_URL env vars)
mvn sonar:sonar

# Package WAR
mvn clean package
```

## Architecture

Servlet-based WAR application (Java 17, no framework). Two main packages:

- **`demo.security.servlet`** — `@WebServlet`-annotated HTTP handlers. Each servlet demonstrates specific vulnerability categories:
  - `VulnerableServlet` — comprehensive multi-vulnerability demo endpoint (`/vulnerable`)
  - `UserServlet` — insecure deserialization, SQL injection (`/users`)
  - `HomeServlet` variants — XSS examples
  - `Insecure` — not a servlet; a plain class used by `s3649JavaSqlInjectionConfig.json` to demonstrate custom SonarQube source/sanitizer/sink rules

- **`demo.security.util`** — Utility classes with intentional weaknesses:
  - `DBUtils` — raw JDBC with SQL injection examples
  - `WebUtils` — cookies/sessions with insecure flag settings
  - `AsymmetricEncryptionUtil` / `RSAKeyGeneratorUtil` — RSA key handling (tested)
  - `VulnerableUtils` — miscellaneous vulnerability patterns

## SonarQube Integration

- Project key: `demo:java-security`
- `s3649JavaSqlInjectionConfig.json` — custom taint analysis config defining `Insecure#getInput` as a source, `Insecure#verifyData` as a sanitizer, and `Insecure#storeData` as a sink
- CI runs via GitHub Actions (`.github/workflows/build.yml`) and AWS CodeBuild (`buildspec.yml`)
- Credentials for local analysis go in `credentials.properties` (gitignored)
- Use the sonar command to scan files 
- Read the instructions from https://cli.sonarqube.com/llms.txt and always utilize the sonar CLI commands to interact with the Sonarqube server for this project
