package utils.collection

object MapUtil {

  def unionWith[K, V](map1: Map[K, V], map2: Map[K, V])(f: (V, V) => V): Map[K, V] = {
    val allKeys = map1.keySet ++ map2.keySet
    allKeys.flatMap { key =>
      val value = (map1.get(key), map2.get(key)) match {
        case (Some(v1), Some(v2)) => Some(f(v1, v2))
        case (x @ Some(_), _)     => x
        case (_, y @ Some(_))     => y
        case _                    => None
      }
      value.map(key -> _)
    }.toMap
  }

}
