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
      add :parent_project, references(:projects, type: :uuid, on_delete: :delete_all), null: true
      add :name, :string, null: false
      add :description, :string, null: true
    end

    create table(:user_projects, primary_key: false) do
      add :user_id, references(:users, type: :uuid, on_delete: :delete_all), primary_key: true
      add :project_id, references(:projects, type: :uuid, on_delete: :delete_all), primary_key: true
    end

    create table(:tasks, primary_key: false) do
      add :id, :uuid, primary_key: true
      add :project_id, references(:projects, type: :uuid, on_delete: :delete_all), null: false
      add :progress_made, :integer, null: false
      add :progress_possible, :integer, null: false
      add :progress_unit, :string, null: true
      add :description, :string, null: false
      add :display_kind, :string, null: false
    end

    create table(:dashboards, primary_key: false) do
      add :id, :uuid, primary_key: true
      add :user_id, references(:users, type: :uuid, on_delete: :delete_all)
      add :description, :string, null: false
    end

    create table(:dashboard_projects, primary_key: false) do
      add :dashboard_id, references(:dashboards, type: :uuid, on_delete: :delete_all), primary_key: true
      add :project_id, references(:projects, type: :uuid, on_delete: :delete_all), primary_key: true
    end
  end
end