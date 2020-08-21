import Config

config :londo, Londo.Repo,
  database: "londo",
  username: "londo",
  password: "X6Bl62$LQ%QE0Ni1$k6k",
  hostname: "localhost"

config :londo, ecto_repos: [Londo.Repo]