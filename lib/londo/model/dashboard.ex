defmodule Londo.Model.Dashboard do
  use Ecto.Schema

  @primary_key {:id, Ecto.UUID, []}
  schema "dashboards" do
    field :user_id, Ecto.UUID
    field :description, :string
  end

end
