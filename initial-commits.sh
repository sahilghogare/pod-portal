#!/usr/bin/env bash
# Creates the initial commit history for the pod-portal repository.
# Run once, from the folder that contains pod-portal/ (needs Git 2.28 or newer).
set -e
cd "$(dirname "$0")/pod-portal"
if [ -d .git ]; then echo "pod-portal is already a Git repository. Stopping."; exit 1; fi

git init -b main

git add .gitignore README.md
git commit -m "chore: initialise repository with README and .gitignore"

git add pom.xml
git commit -m "build: add Maven pom with Spring Boot 3.3 and WAR packaging"

git add src/main/java src/main/resources/templates
git commit -m "feat: add application entry point and home page"

git add src/main/resources/application.properties
git commit -m "chore: configure H2 database and health endpoint"

git add src/test
git commit -m "test: add context load and home page tests"

git add .github CONTRIBUTING.md docs/BRANCH_POLICY.md
git commit -m "docs: add issue templates, PR template and branch policy"

git add docs
git commit -m "docs: add task 1 to 3 project documents"

echo
git status --short
git log --oneline
