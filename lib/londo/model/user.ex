defmodule Londo.Model.User do
  use Ecto.Schema

  schema "user" do
    field :id, :uuid
    field :name, :string
    field :display_name, :string
    field :email, :string
    field :password_hash, :string
    field :password_salt, :string
  end

end
