defmodule Londo.Model.DashboardProject do
  use Ecto.Schema

  schema "dashboard_projects" do
    field :dashboard_id, :uuid
    field :project_id, :uuid
  end

end
