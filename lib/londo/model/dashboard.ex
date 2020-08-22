defmodule Londo.Model.Dashboard do
  use Ecto.Schema

  schema "dashboards" do
    field :id, :uuid
    field :user_id, :uuid
    field :description, :string
  end

end
