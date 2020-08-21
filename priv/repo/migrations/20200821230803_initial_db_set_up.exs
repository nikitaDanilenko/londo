defmodule Londo.Repo.Migrations.InitialDbSetUp do
  use Ecto.Migration

  def change do
    create table(:users, primary_key: false) do
      add :id, :uuid, primary_key: true
      add :name, :string, null: false
      add :display_name, :string, null: true
      add :email, :string, null: false
      add :password_hash, :string, null: false
      add :password_salt, :string, null: false
    end

    create table(:projects, primary_key: false) do
      add :id, :uuid, primary_key: true
      add :parent_project, references(:projects, type :uuid), null: true
      add :name, :string, null: false
      add :description, :string, null: true
    end

    create table(:user_projects, primary_key: false) do
      add :user_id, references(:users, type: uuid), primary_key: true
      add :project_id, references(:projects), primary_key: true
    end

    create table(:tasks, primary_key: false) do
      add :id, :uuid, primary_key: true
    end
  end
end