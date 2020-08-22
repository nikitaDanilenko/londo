# This file is responsible for configuring your application
# and its dependencies with the aid of the Mix.Config module.
#
# This configuration file is loaded before any dependency and
# is restricted to this project.

# General application configuration
use Mix.Config

config :londo,
  ecto_repos: [Londo.Repo]

# Configures the endpoint
config :londo, LondoWeb.Endpoint,
  url: [host: "localhost"],
  secret_key_base: "IaHUPThoZDeH5GjzcncPpUFye7fNlQiHkRD2Xj3v60o/4fzINw3FCZfTebV0xo3u",
  render_errors: [view: LondoWeb.ErrorView, accepts: ~w(html json), layout: false],
  pubsub_server: Londo.PubSub,
  live_view: [signing_salt: "BQPXVwDc"]

# Configures Elixir's Logger
config :logger, :console,
  format: "$time $metadata[$level] $message\n",
  metadata: [:request_id]

# Use Jason for JSON parsing in Phoenix
config :phoenix, :json_library, Jason

# Import environment specific config. This must remain at the bottom
# of this file so it overrides the configuration defined above.
import_config "#{Mix.env()}.exs"
