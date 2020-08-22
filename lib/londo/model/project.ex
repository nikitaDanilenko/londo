defmodule Londo.Model.Project do
  use Ecto.Schema

  schema "projects" do
    field  :id, :uuid
    field  :parent_project, :uuid
    field  :name, :string
    field  :description, :string
  end

end
