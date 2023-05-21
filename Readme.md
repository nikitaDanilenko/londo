# Londo

This tool allows hosting progress tracking of several users. Every user can create projects and make these projects visible to a collection of users. Users can configure dashboards to view the progress of users on a list of selected projects.

The tool's only purpose is displaying and tracking the progress, there is no integration with common project management tools. The main reason for this tool is to show a pretty progress report.

## Naming

The name `londo` hints at [Londo Mollari](https://en.wikipedia.org/wiki/Londo_Mollari), a character from the TV show [Babylon 5](https://en.wikipedia.org/wiki/Babylon_5). In a memorable, but very dark scene the character ecstatically states "Ah, progress!"

## Frontend

* Install [elm-create-app](https://github.com/halfzebra/create-elm-app)
* Run `elm-app start` for development
* Afterwards address the pages using `localhost:3000/#/<page>`

## Backend

### Database
1. Create a database, a corresponding user, and connect the two:
   ```
   psql -U postgres
   psql>create database <londo>;
   psql>create user <londo> with encrypted password <password>;
   psql>grant all privileges on database <londo> to <londo>;
   ```
   When `psql` is running in Docker it may also be necessary to add
   ```
   psql>\c <londo>
   londo>grant all privileges on all tables in schema public to <londo>;
   ```
1. The system scans for migrations in the folder `conf/db/migrations/default`
   and applies new ones.
   After a migration one should re-generate database related code:
   `sbt slickCodegen` generates the base queries, and types.
1. The front end tools for the GraphQL interface are generated via
   `npm run apiGen`.
   Make sure that the back end server is running, so the current schema can be fetched.

## Deployment

1. When running (locally) via Docker, make sure to
   1. Provide all necessary variables in a top-level file called `deployment.env`.
      The variables in question are specified in `application.conf` (back end),
      and in `index.js` (front end).
   2. Duplicate the database components from `deployment.env` to the top-level file `db.env`,
      see the note in `db.env` for more information.