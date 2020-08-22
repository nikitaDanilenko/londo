defmodule Londo.Model.UserProject do
  use Ecto.Schema

  schema "user_projects" do
    field :user_id, Ecto.UUID
    field :project_id, Ecto.UUID
  end

end
