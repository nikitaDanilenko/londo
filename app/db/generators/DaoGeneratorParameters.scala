package db.generators

case class DaoGeneratorParameters(
    typeName: String,
    daoPackage: String,
    keyDescription: KeyDescription,
    columnSearches: List[Column]
)
