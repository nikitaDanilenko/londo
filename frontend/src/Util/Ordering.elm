module Util.Ordering exposing (..)


type alias Ordering k =
    k -> k -> Order


with : (k -> comparable) -> Ordering k
with f x y =
    compare (f x) (f y)
