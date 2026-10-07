#!/usr/bin/env bash
# Creates labels, milestones and the initial backlog issues on GitHub.
# Needs the GitHub CLI (https://cli.github.com). Run `gh auth login` first,
# then run this from inside the pod-portal folder after the repository is pushed.
set -e

for s in 1 2 3 4 5; do
  gh api "repos/{owner}/{repo}/milestones" -f title="Sprint $s" >/dev/null || true
done
gh label create user-story --color 1F77B4 --force
gh label create devops --color 2E7D5B --force
gh label create bug --color D73A4A --force
gh label create must --color B60205 --force
gh label create should --color FBCA04 --force

issue() { gh issue create --title "$1" --body "$2" --label "$3" --milestone "$4"; }

issue "US-01: Login and role-based access" $'As a user, I want to log in with my credentials so that I only see the actions allowed for my role.\n\n- [ ] Valid credentials open the role home page\n- [ ] Invalid credentials show an error\n- [ ] An Agent cannot open Admin-only pages' "user-story,must" "Sprint 2"
issue "US-02: Create delivery record" $'As an Admin, I want to create a delivery record so that every parcel is tracked from the start.\n\n- [ ] Saved with status Created\n- [ ] Missing mandatory field shows a validation message\n- [ ] Duplicate tracking number is rejected' "user-story,must" "Sprint 2"
issue "US-03: Assign delivery to agent" $'As an Admin, I want to assign a delivery to an agent so that responsibility is clear.\n\n- [ ] Created delivery becomes Assigned\n- [ ] Agent name visible on the detail page' "user-story,must" "Sprint 2"
issue "US-04: Agent assigned list" $'As an Agent, I want to see my assigned deliveries so that I know what to deliver.\n\n- [ ] Only my deliveries are listed\n- [ ] Rows show tracking number, customer, address and status' "user-story,must" "Sprint 2"
issue "US-05: Mark delivered with proof" $'As an Agent, I want to mark a delivery as delivered with recipient details so that proof is recorded.\n\n- [ ] Recipient name and note or OTP required\n- [ ] Delivery time stored automatically\n- [ ] Status becomes Delivered' "user-story,must" "Sprint 2"
issue "US-06: Verify or reject proof" $'As a Reviewer, I want to verify or reject submitted proof so that records are trustworthy.\n\n- [ ] Verify sets status Verified\n- [ ] Reject requires a reason and sets status Rejected\n- [ ] Actions only offered for Delivered records' "user-story,must" "Sprint 2"
issue "US-07: Search and filter deliveries" $'As a user, I want to search deliveries by tracking number, customer or status so that I can find records quickly.\n\n- [ ] Matching records listed\n- [ ] No match shows a message\n- [ ] Status filter works' "user-story,must" "Sprint 2"
issue "US-08: Update delivery details" $'As an Admin, I want to update delivery details so that mistakes can be corrected.\n\n- [ ] Editable until Delivered\n- [ ] Verified records cannot be edited' "user-story,should" "Sprint 2"
issue "US-09: Summary dashboard" $'As an Admin, I want a dashboard with counts by status so that I see progress at a glance.\n\n- [ ] Count shown for each status\n- [ ] Counts update after a status change' "user-story,must" "Sprint 2"
issue "US-10: Status workflow validation" $'As the system owner, I want the status workflow enforced so that steps cannot be skipped.\n\n- [ ] Invalid transitions are blocked with a message' "user-story,should" "Sprint 2"
issue "TS-01: Git repository and branch policy" "Repository with README, .gitignore, templates and a documented branch policy." "devops,must" "Sprint 2"
issue "TS-02: Jenkins CI build job" "Jenkins job that builds the project on every commit and archives the artefact." "devops,must" "Sprint 3"
issue "TS-03: Jenkinsfile and Tomcat deployment" "Pipeline as code with checkout, build, package and deploy stages." "devops,must" "Sprint 3"
issue "TS-04: Selenium test suite" "Three to five Selenium user journeys with assertions and failure screenshots." "devops,must" "Sprint 3"
issue "TS-05: Dockerfile and container lifecycle" "Dockerfile, image build, run, logs, stop and remove." "devops,must" "Sprint 4"
issue "TS-06: Jenkins-Docker continuous deployment" "Versioned image published and a fresh container deployed after tests pass." "devops,must" "Sprint 4"
issue "TS-07: Ansible or Puppet configuration" "Configuration script for packages, users, folders, ports and services." "devops,must" "Sprint 5"
issue "TS-08: Provisioning, health check and rollback" "Clean-node provisioning, idempotency proof, health check and rollback." "devops,must" "Sprint 5"
