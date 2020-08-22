defmodule Londo.Model.User do
  use Ecto.Schema

  @primary_key {:id, Ecto.UUID, []}
  schema "user" do
    field :name, :string
    field :display_name, :string
    field :email, :string
    field :password_hash, :string
    field :password_salt, :string
  end

end
