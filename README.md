# Proof-of-Delivery Portal

A web application for recording parcel deliveries, capturing proof of delivery, and reviewing delivery outcomes. This college DevOps project demonstrates a Git-based workflow and an automated CI/CD pipeline using **Jenkins, Maven, Selenium, Docker, Docker Hub, and Ansible**.

> College DevOps mini-project, BE Computer Engineering (CMPN A). Author: Sahil Suresh Ghogare.

## MVP features

- Login with three roles: Admin, Delivery Agent, Reviewer
- Create, view, update, and search deliveries
- Role-based status workflow: `Created > Assigned > Out for Delivery > Delivered > Verified / Rejected`
- Proof capture (recipient name, time, note or OTP)
- Dashboard with delivery counts by status

## Tech stack

| Area | Choice |
|---|---|
| Language / framework | Java 17, Spring Boot 3.3, Thymeleaf |
| Persistence | Spring Data JPA, H2 (file mode) |
| Build | Maven 3.9 |
| Packaging | Executable WAR |
| Testing | JUnit 5, Selenium WebDriver |
| CI/CD | Git, GitHub, Jenkins, Docker, Docker Hub, Ansible |

## Getting started

Prerequisites: JDK 17+, Maven 3.9+, Git.

```bash
git clone https://github.com/sahilghogare/pod-portal.git
cd pod-portal
mvn clean package
java -jar target/pod-portal.war
```

Open http://localhost:8080/ and check http://localhost:8080/actuator/health (expected: `{"status":"UP"}`). To run from source, use `mvn spring-boot:run`.

## Project structure

```
src/main/java/edu/podportal/      application code (controller, service, repository, model)
src/main/resources/templates/     Thymeleaf pages
src/test/java/edu/podportal/      unit and Selenium tests
ansible/                          Ansible inventory and provisioning playbook
docs/                             project documents and branch policy
.github/                          issue templates and pull request template
Jenkinsfile                       Jenkins CI/CD pipeline
Dockerfile                        application container image
```

## Git workflow

The project uses `main` as the stable/release branch and `develop` as the integration branch. Feature, fix, and maintenance work is developed on short-lived branches and proposed through pull requests. See [docs/BRANCH_POLICY.md](docs/BRANCH_POLICY.md) and [CONTRIBUTING.md](CONTRIBUTING.md).

## CI/CD workflow

The Jenkins pipeline is configured to poll SCM every five minutes. It checks out the repository, compiles and packages the WAR with Maven, runs the Selenium test profile, builds a Docker image, pushes versioned and `latest` tags to Docker Hub, then deploys a container and checks the application login endpoint. The Docker build/push/deploy stages run only if earlier stages succeed, so a failing Selenium test prevents a new image from being deployed.

The Jenkinsfile currently deploys the container from the Jenkins agent. Ansible is maintained separately for target-node provisioning; do not interpret the Jenkins deployment stage as an Ansible-driven deployment.

## Ansible provisioning

The playbook in `ansible/site.yml` installs Docker and supporting packages, ensures the `deploy` user exists, creates the application/data directories and environment file, and enables the Docker service. Ansible tasks are designed to be idempotent; running the playbook again should report no changes when the target is already configured.

## Roadmap

| # | Task | Status |
|---|---|---|
| 1 | Problem definition and scope | Done |
| 2 | Agile planning and DevOps workflow | Done |
| 3 | Requirements, architecture and setup | Done |
| 4 | Git and GitHub repository | Done |
| 5–6 | Feature branches and MVP completion | Done |
| 7–8 | Jenkins CI and pipeline setup | Done |
| 9–10 | Selenium tests and continuous testing | Done |
| 11–12 | Docker and Jenkins-Docker pipeline stages | Done |
| 13–14 | Ansible provisioning/configuration management | Done |
| 15 | Final release, documentation and viva | In progress |

## Known limitations

- H2 file-based persistence is intended for a small single-node demonstration, not a highly available production database.
- Jenkins uses SCM polling rather than webhooks.
- The pipeline uses a configured Jenkins agent with Windows batch/PowerShell commands.
- Selenium coverage is limited to the automated journeys implemented in the test suite.
- HTTPS, centralized monitoring, and multi-node high availability are outside the current scope.

## Release evidence

The final release tag and GitHub Release link will be added here after the final integration has been reviewed and the release is published. Until then, use the repository's [Releases page](https://github.com/sahilghogare/pod-portal/releases) to see the published versions.
