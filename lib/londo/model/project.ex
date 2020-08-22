defmodule Londo.Model.Project do
  use Ecto.Schema

  @primary_key {:id, Ecto.UUID, []}
  schema "projects" do
    field  :parent_project, Ecto.UUID
    field  :name, :string
    field  :description, :string
  end

end
