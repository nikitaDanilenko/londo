defmodule Londo.Application do

  def start(_type, _args) do
    # List all child processes to be supervised
    children = [
      Friends.Repo,
    ]
  end

end