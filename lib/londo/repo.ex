defmodule Londo.Repo do
  use Ecto.Repo,
    otp_app: :londo,
    adapter: Ecto.Adapters.Postgres
end
