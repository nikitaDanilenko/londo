defmodule LondoTest do
  use ExUnit.Case
  doctest Londo

  test "greets the world" do
    assert Londo.hello() == :world
  end
end
