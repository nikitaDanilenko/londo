package services.dao

import scala.reflect.macros.whitebox.{ Context => MacroContext }

class DAOMacroImplementation(val c: MacroContext) {
  import c.universe._

  def insertAction_impl[Row](row: Tree)(implicit t: WeakTypeTag[Row]): Tree = {
    q"""
        import ${c.prefix}._
        
        quote {
          query[$t].insert(lift($row)).returning(x => x)
        }
      """
  }

  def insertAllAction_impl[Row](rows: Tree)(implicit t: WeakTypeTag[Row]): Tree = {
    q"""
        import ${c.prefix}._
        
        quote {
          liftQuery($rows).foreach(query[$t].insert(_))
        }
      """
  }

  def findAction_impl[Key, Row](key: Tree, keyOf: Tree)(implicit t: WeakTypeTag[Row]): Tree = {
    q"""
        import ${c.prefix}._
        
        quote {
          query[$t].filter(e => $keyOf(e) == $key)
        }
      """
  }

  def findAllAction_impl[Key, Row](keys: Tree, keyOf: Tree)(implicit t: WeakTypeTag[Row]): Tree = {
    q"""
        import ${c.prefix}._
        
        val idSet = $keys.toSet
        quote {
          query[$t].filter(e => idSet.contains($keyOf(e)))
        }
      """
  }

  def deleteAction_impl[Key, Row](key: Tree, keyOf: Tree)(implicit t: WeakTypeTag[Row]): Tree = {
    q"""
        import ${c.prefix}._
        
        quote {
          query[$t].filter(e => $keyOf(e) == $key).delete.returning(x => x)
        }
      """
  }

  def deleteAllAction_impl[Key, Row](keys: Tree, keyOf: Tree)(implicit t: WeakTypeTag[Row]): Tree = {
    q"""
        import ${c.prefix}._
        
        val idSet = $keys.toSet
        quote {
          query[$t].filter(e => idSet.contains($keyOf(e))).delete
        }
      """
  }

  def updateAction_impl[Key, Row](row: Tree, keyOf: Tree)(implicit t: WeakTypeTag[Row]): Tree = {
    q"""
        import ${c.prefix}._
        
        quote {
          query[$t].filter(e => $keyOf(e) == $keyOf($row)).update($row).returning(x => x)
        }
      """
  }

}
