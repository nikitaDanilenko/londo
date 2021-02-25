package services.dao

import doobie.quill.DoobieContext
import io.getquill.{ ActionReturning, BatchAction, Insert }

import scala.language.experimental.macros

trait MacroDAO {
  self: DoobieContext.Postgres[_] =>

  def insertAction[Row](row: Row): Quoted[ActionReturning[Row, Row]] =
    macro DAOMacroImplementation.insertAction_impl[Row]

  def insertAllAction[Row](rows: Seq[Row]): Quoted[BatchAction[Insert[Row]]] =
    macro DAOMacroImplementation.insertAllAction_impl[Row]

  def findActionExplicit[Key, Row](key: Key, keyOf: Row => Key): Quoted[ActionReturning[Row, Row]] =
    macro DAOMacroImplementation.findAction_impl[Key, Row]

  def findAllActionExplicit[Key, Row](keys: Seq[Key], keyOf: Row => Key): Quoted[ActionReturning[Row, Row]] =
    macro DAOMacroImplementation.findAllAction_impl[Key, Row]

  def deleteActionExplicit[Key, Row](key: Key, keyOf: Row => Key): Quoted[ActionReturning[Row, Row]] =
    macro DAOMacroImplementation.deleteAction_impl[Key, Row]

  def deleteAllActionExplicit[Key, Row](keys: Seq[Key], keyOf: Row => Key): Quoted[ActionReturning[Row, Row]] =
    macro DAOMacroImplementation.deleteAllAction_impl[Key, Row]

  def updateActionExplicit[Key, Row](row: Row, keyOf: Row => Key): Quoted[ActionReturning[Row, Row]] =
    macro DAOMacroImplementation.updateAction_impl[Key, Row]

}
