# Proof-of-Delivery Portal

A small web application for recording parcel deliveries, capturing proof of delivery and reviewing it,
built as the base for a complete DevOps pipeline: **Git, Jenkins, Selenium, Docker and Ansible/Puppet**.

> College DevOps mini-project, BE Computer Engineering (CMPN A). Author: Sahil Suresh Ghogare.

## MVP features
- Login with three roles: Admin, Delivery Agent, Reviewer
- Create, view, update and search deliveries
- Role-based status workflow: `Created > Assigned > Out for Delivery > Delivered > Verified / Rejected`
- Proof capture (recipient name, time, note or OTP)
- Dashboard with delivery counts by status

## Tech stack
| Area | Choice |
|---|---|
| Language / framework | Java 17, Spring Boot 3.3, Thymeleaf |
| Persistence | Spring Data JPA, H2 (file mode) |
| Build | Maven 3.9 |
| Server | Tomcat 10.1 (WAR), later Docker |
| Testing | JUnit 5, Selenium WebDriver |
| CI/CD | Git, GitHub, Jenkins, Docker, Ansible or Puppet |

## Getting started
Prerequisites: JDK 17+, Maven 3.9+, Git.

```
git clone <repository-url>
cd pod-portal
mvn clean package
java -jar target/pod-portal.war
```

Open http://localhost:8080/ and check http://localhost:8080/actuator/health (expected: `{"status":"UP"}`).
To run from source: `mvn spring-boot:run`.

To deploy on an external Tomcat 10.1, copy `target/pod-portal.war` into `webapps/` and open
http://localhost:8080/pod-portal/.

## Project structure
```
src/main/java/edu/podportal/      application code (controller, service, repository, model)
src/main/resources/templates/     Thymeleaf pages
src/main/resources/               application.properties
src/test/java/edu/podportal/      unit and Selenium tests
docs/                             project documents and branch policy
.github/                          issue templates and pull request template
```

## Workflow
Work follows the branching model and commit convention described in
[docs/BRANCH_POLICY.md](docs/BRANCH_POLICY.md) and [CONTRIBUTING.md](CONTRIBUTING.md).

## Roadmap
| # | Task | Status |
|---|---|---|
| 1 | Problem definition and scope | Done |
| 2 | Agile planning and DevOps workflow | Done |
| 3 | Requirements, architecture and setup | Done |
| 4 | Git and GitHub repository | In progress |
| 5-6 | Feature branches and MVP completion | Planned |
| 7-8 | Jenkins CI and Jenkinsfile deployment | Planned |
| 9-10 | Selenium tests and continuous testing | Planned |
| 11-12 | Docker and Jenkins-Docker CD | Planned |
| 13-14 | Ansible/Puppet and provisioning | Planned |
| 15 | Final release, documentation and viva | Planned |
