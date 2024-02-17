# AiR WBL Mobilisis - WattsUp

This is a Work-Based Learning (WBL) project developed in collaboration with Mobilisis for the course Program Analysis and Development (Analiza i razvoj programa). 'WattsUp' is an Android mobile application designed for managing the charging of personal electric cars and providing an overview of charging statistics. Additionally, an Angular web application is created that enables users to access their charging statistics, locations, and other information about available chargers in a specific area. The web application also allows administrators to perform CRUD (Create, Read, Update, Delete) operations on charger data and user data. The backend is implemented in .NET Framework and PostgreSQL. 

## Technologies

- Docker Desktop (installation link and instructions: https://www.docker.com/products/docker-desktop/)
- Visual Studio Code, if using as IDE for Angular project (installation link and instructions: https://code.visualstudio.com/download)
- Visual Studio 2022, IDE for ASP.NET project (installation link and instructions: https://visualstudio.microsoft.com/vs/community/)
- Android Studio (Giraffe, installation link: https://developer.android.com/studio/install)
- PostgreSQL (installation link and instructions: https://www.postgresql.org/download/)

## Test data for login

Administrator - Username: **admin** Password: **123456**
Ordinary user - Username: **user** Password: **123456**

Certain web pages are available only with admin account, so it's suggested to use admin credentials to test full functionality of the app.

## Instructions for starting the projects

### Android project

Open the project inside android directory from `dev-android` branch in Android Studio. 

Backend is currently hosted online so no need to run Docker first. If you still want to use Docker, before running the Android app, in code it is necessary to change the BASE_URL address to the local IP address of the current machine, since `localhost` won't work (see the end of README).

### Angular project

Web project can be tested on: https://wattsup.onrender.com/

Alternatively, if you want to run it locally, you have an option to run locally with running local backend first, or running locally with hosted backend, using the commands below.

In the web directory:
```
npm install                           # Only after the first fresh pull from the repository, to update node modules
ng serve                              # Builds and serves web application locally, you will need to host backend and database locally to for this to work
ng serve --configuration=production   # Builds and serves web application locally and uses backend that is already hosted on server ASP.NET project
```

### .NET project

Currently, backend is hosted on Render service (https://wattsup-backend.onrender.com), so running locally isn’t necessary. Simply run Android project since it connects to this API. Likewise, web project can be accessed on https://wattsup.onrender.com/ and it also connects to this API.

Running backend locally:

Before running .NET project you have to have Docker Desktop installed and you have to run Docker database (see below).

Open the project inside backend directory from `dev-backend` branch in Visual Studio. You can build and run project in Docker by pressing on the green button at top with the selected option Docker.

### Docker Database

From backend directory, to create Docker container:
```
docker run --name air-database -e POSTGRES_PASSWORD=Ho1hmRPnKSjTmgS -p 32769:5432 -d postgres:15
```

This will create Docker container with Postgres database. Now you can run .NET project which creates another container in which the backend is executed. The backend will connect to the database and add tables with mock data (via Migrations). For testing of login, you can use this user that is added to database via Migrations:
```
{
	"Id" : 3,
	"FirstName" : "John",
	"LastName" : "Doe",
	"Email" : "john.doe@gmail.com",
		//Password is 123456 when not hashed
	"Password" : "AQAAAAIAAYagAAAAEE0LVQ5mGf8bCCIJ\/r3vv4TD3w+MkMgw+wXZIYveI7zwcBJWWdFs9AmwNwOlHpcfNw==",
	"Active" : true,
	"CreatedAt" : "2023-11-08T16:41:33.750Z",
	"RoleId" : 2,
	"RefreshTokenId" : 1
}
```

### Connecting to API

Before migration to always-on server, connecting to API was done on local machines, with Docker database and ASP.NET project running locally. Angular project uses IP address localhost, so no need to adjust it. As for Android project, since localhost or 10.0.0.2 do not work there for API connection, before running the mobile application, in BASE_URL inside NetworkService class it is necessary to change the address to the local IP address of the current machine (the port used for the backend remains the same, 32770).

Update 20/12/2023: backend has been hosted on render.com.
