# Londo

The application allows hosting progress tracking with a focus on completion rates.
Every account can create projects,
and connect these projects to dashboards.
Dashboards then provide statistics views,
which can be shared publicly if desired.

The tool's only purpose is displaying and tracking the progress,
there is no integration with common project management tools.
The main reason for this tool is to show a pretty progress report.

## Naming

The name `londo` hints at [Londo Mollari](https://en.wikipedia.org/wiki/Londo_Mollari), a character from the TV show [Babylon 5](https://en.wikipedia.org/wiki/Babylon_5). In a memorable, but very dark scene the character ecstatically states "Ah, progress!"

# Development

## Front End

* Install [elm-create-app](https://github.com/halfzebra/create-elm-app)
* Run `elm-app start` for development
* View the pages using `localhost:3000/#/<page>`

## Back End

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

# Takeaways from development

1. GraphQL in Elm is a pleasure.
   While the Scala counterpart took some minor figuring out,
   I still very much liked the overall design in the end.
   One particularly interesting option is to annotate the GraphQL schema
   and have the documentation presented in a nice way.
2. In the future it may make sense to drop the `Future` based service
   level functions altogether, and to only use `DBIO`.
   In particular, in combination with GraphQL one gets a good control
   over atomicity.
3. The Elm support for arbitrary precision numbers, and numeric operations for those is currently quite weak.
   All statistics have been hence moved to the back end.
   Generally, it makes sense to compute them in the front end,
   but the operations are extremely slow, 
   which makes interaction with the statistics view virtually unusable.
4. Using proper semantic structuring for HTML provides a lot of automatically well-designed styling decisions.
