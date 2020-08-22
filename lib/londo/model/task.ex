defmodule Londo.Model.Task do
  use Ecto.Schema

  schema "tasks" do
    field :id, :uuid
    field :project_id, :uuid
    field :progress_made, :integer
    field :progress_possible, :integer
    field :progress_unit, :string
    field :description, :string
    field :display_kind, :string
  end

end