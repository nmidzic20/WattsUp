# AiR WBL Mobilisis - WattsUp

This is a Work-Based Learning (WBL) project developed in collaboration with Mobilisis for the course Program Analysis and Development (Analiza i razvoj programa). 'WattsUp' is an Android mobile application designed for managing the charging of personal electric cars and providing an overview of charging statistics. Additionally, an Angular web application is created that enables users to access their charging statistics, locations, and other information about available chargers in a specific area. The web application also allows administrators to perform CRUD (Create, Read, Update, Delete) operations on charger data and user data. The backend is implemented in ASP.NET and PostgreSQL.

## Instructions for starting the projects

### Android project

Open the project inside `android` directory from `dev-android` branch in Android Studio.

### Angular project

In the `web` directory:
```
npm install      # Only after the first fresh pull from the repository, to update node modules
ng serve
```

### ASP.NET project

Before running .NET project you have to have Docker installed and you have to run Docker database (see [here](#docker-database)).

Open the project inside `backend` directory from `dev-backend` branch in Visual Studio. You can build and run project in docker by pressing on button at top that says `Docker`.

## Git workflow

Each team member should have three separate folders (e.g. AIR-Mobile, AIR-Web, AIR-Backend) and clone this repository three times to each of them (`git clone`). Each folder should always be on the `dev` branch for that technology, e.g. AIR-Mobile is always on `dev-android`, AIR-Web on `dev-web` etc. Whenever working on a feature, open the appropriate folder and IDE depending on the technology, e.g. if working on a mobile feature, open the project from AIR-Mobile in Android Studio and whenever committing, all commits will be made in local repository inside AIR-Mobile.

No pull requests or direct commits to `main` branch, all pull requests are made to either `dev-android`, `dev-web` or `dev-backend` branches. These three branches are merged into `main` before the project defenses or at the end of the production cycle. Only `dev` branches may be merged into `main`, all other branches (feature) may only be merged into one of the `dev` branches. Each merged PR to `main` represents a version of production code and has a tag, e.g. `1.0`, `1.0.1`, `1.1.0`.

All feature branches are created from the respective `dev` branch for that technology (mobile, web or backend), never from `main`. E.g. for implementing Login screen on web, create feature branch `dev_web/ANP-7_login` from `dev-web` and when finished make PR to `dev-web`, for implementing login routes in backend, create feature branch `dev_backend/ANP-30_login` from `dev-backend` and when finished make PR to `dev-backend`, etc.

If necessary, `hotfix` branch may be created from `main`. When the issue is patched, `hotfix` must be merged into both `main` and the `dev` branch corresponding to the technology the fix was made in.

Whenever creating a feature branch, always make sure to get the latest version from the respective `dev` branch (`git pull`) before creating the branch from it.

`dev` branches may have direct commits made to them only if it is a very small commit for which creating a new branch doesn't make sense.

Whenever creating a pull request from a feature branch to one of `dev` branches, two other team members must be assigned as reviewers for that PR. They should review the code, leave the comments and either approve the request or request changes. PR should be resolved the same or the next day, if the reviewers take any longer than that, then the creator of PR approves their own request to merge the code (avoid as much as possible).

## Naming convention

Each branch, commit and PR should contain reference to Jira issue in order for them to automatically be shown in Jira. Note that Jira issues are case-sensitive, so if the issue is `ANP-1`, then `anp-1` won't work for linking to the issue. More details can be found on https://support.atlassian.com/jira-software-cloud/docs/reference-issues-in-your-development-work/

### Branches

Naming a feature branch created from `dev-android`: `dev_android/[Jira_issue_ID]_feature-name`, e.g. `dev_android/ANP-1_login` 

Naming a feature branch created from `dev-web`:  `dev_web/[Jira_issue_ID]_feature-name`, e.g. `dev_web/ANP-2_login`

Naming a feature branch created from `dev-backend`:  `dev_backend/[Jira_issue_ID]_feature-name`, e.g. `dev_backend/ANP-3_login` 

### Commits

Each commit message should start with Jira issue ID, e.g. "ANP-3 Implemented login route"

### Pull Requests

Each PR name should include Jira issue ID.

### Git hooks

To enforce standardised naming convention, include files `commit-msg` and `pre-push` inside `.git\hooks` directory in each local repository. `commit-msg` script will block a commit if it doesn't start with Jira issue ID (ANP-). `pre-push` script will block push to remote if the branch name doesn't follow naming convention for a feature branch as specified above.

`commit-msg` script:

```
#!/bin/sh
pattern="^ANP-[0-9]+.*"
while read -r line; do
  if ! [[ $line =~ $pattern ]]; then
    echo "Error: Commit message does not begin with 'ANP-'. Commit message was: '$line'"
    exit 1
  fi
done < "$(git rev-parse --git-dir)/COMMIT_EDITMSG"
```

`pre-push` script:

```
#!/bin/sh

remote="$1"
url="$2"

# Define the allowed branch name formats
allowed_formats="(dev_android/ANP-[number]_[string with any characters], dev_backend/ANP-[number]_[string with any characters], dev_web/ANP-[number]_[string with any characters])"

# Iterate over the branch references being pushed
while read local_ref local_sha1 remote_ref remote_sha1; do
    # Extract the branch name from the reference
    branch_name="${local_ref#refs/heads/}"

    # Check if the branch name matches the allowed formats
    if ! [[ $branch_name =~ ^(dev_android/ANP-.+|dev_backend/ANP-.+|dev_web/ANP-.+)$ ]]; then
        echo "Error: Branch name '$branch_name' does not match the allowed formats. Allowed formats: $allowed_formats" >&2
        exit 1
    fi
done
```

## Docker Database

From `backend` directory, to execute Dockerfile:

```
docker run --name air-database -e POSTGRES_PASSWORD=Ho1hmRPnKSjTmgS -p 32769:5432 -d postgres:15
```
This will create Docker container with Postgres database. Now you can run .NET project which creates another container in which the backend is executed. The backend will connect to the database and add tables with mock data (via Migrations).

