defmodule Londo.Model.DashboardProject do
  use Ecto.Schema

  schema "dashboard_projects" do
    field :dashboard_id, Ecto.UUID
    field :project_id, Ecto.UUID
  end

end
