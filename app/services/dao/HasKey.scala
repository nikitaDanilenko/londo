package services.dao

trait HasKey[Row, Key] {

  def keyOf(row: Row): Key

}

object HasKey {
  def apply[Row, Key](implicit hasKey: HasKey[Row, Key]): HasKey[Row, Key] = hasKey
}
