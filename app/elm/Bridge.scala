package elm

import better.files._
import scala.reflect.runtime.universe.TypeTag
import bridges.core.Type.Ref
import bridges.core._
import bridges.core.syntax._
import bridges.elm._
import shapeless.Lazy

object Bridge {

  val elmModule: String = "Api.Types"
  val elmModuleFilePath: File = "frontend" / "src" / "Api" / "Types"

  private val replacementMap = Map(
    Ref("UUID") -> TypeReplacement(
      "Uuid",
      imports = s"\nimport LondoGQL.Scalar exposing (Uuid)",
      encoder = "LondoGQL.Scalar.defaultCodecs.codecUuid.encoder",
      decoder = "LondoGQL.Scalar.defaultCodecs.codecUuid.decoder"
    ),
    Ref("Positive") -> TypeReplacement(
      "Positive",
      imports = s"\nimport LondoGQL.Scalar exposing (Positive)",
      encoder = "LondoGQL.Scalar.defaultCodecs.codecPositive.encoder",
      decoder = "LondoGQL.Scalar.defaultCodecs.codecPositive.decoder"
    ),
    Ref("Natural") -> TypeReplacement(
      "Natural",
      imports = s"\nimport LondoGQL.Scalar exposing (Natural)",
      encoder = "LondoGQL.Scalar.defaultCodecs.codecNatural.encoder",
      decoder = "LondoGQL.Scalar.defaultCodecs.codecNatural.decoder"
    ),
    Ref("Unit") -> TypeReplacement(
      "Unit",
      imports = s"\nimport LondoGQL.Scalar exposing (Unit)",
      encoder = "LondoGQL.Scalar.defaultCodecs.codecUnit.encoder",
      decoder = "LondoGQL.Scalar.defaultCodecs.codecUnit.decoder"
    )
  )

  private def mkElmBridge[A](implicit
      tpeTag: TypeTag[A],
      encoder: Lazy[Encoder[A]]
  ): (String, String) = {
    val (fileName, content) = Elm.buildFile(
      module = elmModule,
      decl = decl[A],
      customTypeReplacements = replacementMap
    )
    fileName ->
      /* The bridge library puts a no longer existing function call here,
         which is why we manually replace it with the correct function.*/
      content.replaceAll(" decode ", " Decode.succeed ")
  }

  def mkAndWrite[A](implicit
      tpeTag: TypeTag[A],
      encoder: Lazy[Encoder[A]]
  ): Unit = {
    val (filePath, content) = mkElmBridge[A]
    val file = (
      elmModuleFilePath /
        filePath
    ).createIfNotExists(createParents = true)
    file.write(content)
  }

}
