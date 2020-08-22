defmodule Londo.Model.UserProject do
  use Ecto.Schema

  schema "user_projects" do
    field :user_id, :uuid
    field :project_id, :uuid
  end

end
