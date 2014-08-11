package pgentity

import anorm.RowParser

object pg_entity {
  case class PgField(name: String, annotation: Option[String] = None, isPk: Boolean = false)

  trait PgEntity[A] {
    def tableName: String
    def columns: List[PgField]
    def parser(prefix: String): RowParser[A]
  }

  def columnList[A](prefix: Option[String])(implicit ev: PgEntity[A]) = {
    val p = prefix.getOrElse(ev.tableName + ".")
    ev.columns.map(p + _.name).mkString(", ")
  }
  def tableName[A](implicit ev: PgEntity[A]) = ev.tableName
  def primaryKeys[A](implicit ev: PgEntity[A]) = ev.columns.filter(_.isPk)
  def columns[A](implicit ev: PgEntity[A]) = ev.columns

  def selectSQL[A](implicit ev: PgEntity[A]) = {
    val tablename = ev.tableName
    val columns = ev.columns.map(_.name).mkString(",")
    s"select $columns from $tablename"
  }

  def prefixedSelectSQL[A](implicit ev: PgEntity[A]) = {
    val tablename = ev.tableName
    val columns = ev.columns.map(c => tablename + "." + c.name).mkString(",")
    s"select $columns from $tablename"
  }

  def insertSQL[A](implicit ev: PgEntity[A]) = {
    val tablename = ev.tableName
    val columns = ev.columns.map(_.name).mkString("(", ",", ")")
    val values = ev.columns.map(c =>
      "{" + c.name + "}" + (c.annotation map(a => "::" + a) getOrElse "")
    ).mkString("(", ",", ")")
    s"insert into $tableName $columns values $values"
  }

  def updateSQL[A](ignored: List[String] = List())(implicit ev: PgEntity[A]) = {
    val tablename = ev.tableName
    val columns = ev.columns.tail.filterNot(c => ignored.contains(c.name))
    val updates = columns.map(c =>
      c.name + " = " + "{" + c.name + "}" + (c.annotation map(a => "::" + a) getOrElse "")
    ).mkString(", ")
    val pkClause = primaryKeys[A].map { case PgField(pk, _, _) =>
      s"$pk = {$pk}"
    }.mkString("", " and ", "")
    s"UPDATE $tablename SET $updates WHERE $pkClause"
  }

  def deleteSQL[A](implicit ev: PgEntity[A]) = {
    val tablename = ev.tableName
    val pkClause = primaryKeys[A].map { case PgField(pk, _, _) =>
      s"$pk = {$pk}"
    }.mkString("", " and ", "")
    s"DELETE FROM $tablename WHERE $pkClause"
  }

  def parser[A](prefix: Option[String] = None)(implicit ev: PgEntity[A]) = {
    ev.parser(prefix getOrElse (ev.tableName + "."))
  }
}
