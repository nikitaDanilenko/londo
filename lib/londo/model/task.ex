defmodule Londo.Model.Task do
  use Ecto.Schema

  @primary_key {:id, Ecto.UUID, []}
  schema "tasks" do
    field :project_id, Ecto.UUID
    field :progress_made, :integer
    field :progress_possible, :integer
    field :progress_unit, :string
    field :description, :string
    field :display_kind, :string
  end

end