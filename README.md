# AiR WBL Mobilisis - Watt's Up

This is a Work-Based Learning (WBL) project developed in collaboration with Mobilisis for the course Program Analysis and Development (Analiza i razvoj programa). 'Watt's Up' is an Android mobile application designed for managing the charging of personal electric cars and providing an overview of charging statistics. Additionally, an Angular web application is created that enables users to access their charging statistics, locations, and other information about available chargers in a specific area. The web application also allows administrators to perform CRUD (Create, Read, Update, Delete) operations on charger data and user data.

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

Open the project inside `backend` directory from `dev-backend` branch in Visual Studio.

## Git workflow

No pull requests or direct commits to `main` branch, all pull requests are made to either `dev-android`, `dev-web` or `dev-backend` branches. These three branches are merged into `main` before the project defenses or at the end of the production cycle. Only `dev` branches may be merged into `main`, all other branches (feature) may only be merged into one of the `dev` branches. Each merged PR to `main` represents a version of production code and has a tag, e.g. `1.0`, `1.0.1`, `1.1.0`.

All feature branches are created from the respective `dev` branch for that technology (mobile, web or backend), never from `main`. E.g. for implementing Login screen on web, create feature branch `dev_web/ANP-7_login` from `dev-web` and when finished make PR to `dev-web`, for implementing login routes in backend, create feature branch `dev_backend/ANP-30_login` from `dev-backend` and when finished make PR to `dev-backend`, etc.

If necessary, `hotfix` branch may be created from `main`. When the issue is patched, `hotfix` must be merged into both `main` and the `dev` branch corresponding to the technology the fix was made in.

Whenever creating a feature branch, always make sure to get the latest version from the respective `dev` branch (`git pull`) before creating the branch from it.

`dev` branches may have direct commits made to them only if it is a very small commit for which creating a new branch doesn't make sense.

Whenever creating a pull request from a feature branch to one of `dev` branches, two other team members must be assigned as reviewers for that PR. They should review the code, leave the comments and either approve the request or request changes. PR should be resolved the same or the next day, if the reviewers take any longer than that, then the creator of PR approves their own request to merge the code (avoid as much as possible).

## Naming convention

Each branch, commit and PR should contain reference to Jira issue in order for them to automatically be shown in Jira.

### Branches

Naming a feature branch created from `dev-android`: `dev_android/[Jira_issue_ID]_feature_name`, e.g. `dev_android/ANP-1_login` 

Naming a feature branch created from `dev-web`:  `dev_web/[Jira_issue_ID]_feature_name`, e.g. `dev_web/ANP-2_login`

Naming a feature branch created from `dev-backend`:  `dev_backend/[Jira_issue_ID]_feature_name`, e.g. `dev_backend/ANP-3_login` 

### Commits

Each commit message should start with Jira issue ID, e.g. "ANP-3 Implemented login route"

### Pull Requests

Each PR name should start with Jira issue ID, e.g. "ANP-3 ..."

## Database and Docker
