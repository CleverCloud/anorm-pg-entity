package pgentity

import anorm.RowParser

object pg_entity {
  // Case class modelling an entity field
  // You can optionally provide a type annotation for types entered as a
  // string in the query (eg like UUID or JSON)
  // A field can be part of the entity's private key.
  case class PgField(name: String, annotation: Option[String] = None, isPk: Boolean = false) {
    val annotationTag = annotation map ("::" + _) getOrElse ""
    val insertPlaceHolder = {
      s"{$name}$annotationTag"
    }
    val updatePlaceHolder = {
      s"$name={$name}$annotationTag"
    }
  }

  // Type Class modelling the mapping between an entity and its DB
  // representation
  trait PgEntity[A] {

    // The name of the table modelled by the entity
    def tableName: String

    // The columns of the table
    def columns: List[PgField]

    // A parser transforming a row into an entity
    // The prefix is used to avoid ambiguities when parsing a row comprised of
    // several entities
    def parser(prefix: String): RowParser[A]
  }

  // List of fields suitable for use in a SELECT query
  def columnList[A](prefix: Option[String])(implicit ev: PgEntity[A]): String = {
    val p = prefix.getOrElse(ev.tableName + ".")
    ev.columns.map(p + _.name).mkString(", ")
  }

  // Helper function to retrieve the name of the table associated to the
  // entity
  def tableName[A](implicit ev: PgEntity[A]): String = ev.tableName

  // Helper function to retrieve the fields of the entity
  def columns[A](implicit ev: PgEntity[A]): List[PgField] = ev.columns

  // Helper function to retrieve a RowParser of the entity
  // You can optionally pass a prefix for the column names.
  // By default it's the table name, in order to avoid ambiguities when
  // parsing results from a join.
  def parser[A](prefix: Option[String] = None)(implicit ev: PgEntity[A]) = {
    ev.parser(prefix getOrElse (ev.tableName + "."))
  }

  // The fields composing the primary key
  def primaryKeys[A](implicit ev: PgEntity[A]): List[PgField] = ev.columns.filter(_.isPk)


  // Body of a SELECT query with no WHERE clause.
  def selectSQL[A](implicit ev: PgEntity[A]): String = {
    val tablename = ev.tableName
    val columns = ev.columns.map(_.name).mkString(",")
    s"select $columns from $tablename"
  }

  // Body of a SELECT query with no WHERE clause.
  // The column names are prefixed with the table names to avoid ambiguities
  def prefixedSelectSQL[A](implicit ev: PgEntity[A]): String = {
    val tablename = ev.tableName
    val columns = ev.columns.map(c => s"$tablename.${c.name}").mkString(",")
    s"select $columns from $tablename"
  }

  // Body of an INSERT command
  def insertSQL[A](implicit ev: PgEntity[A]): String = {
    val tablename = ev.tableName
    val columns = ev.columns.map(_.name).mkString("(", ",", ")")
    val values = ev.columns.map(_.insertPlaceHolder).mkString("(", ",", ")")
    s"insert into $tableName $columns values $values"
  }

  // Body of an UPDATE command for the whole entity
  // You can pass a list of fields to leave out from the update
  // The fields used in the primary key are automatically left out from the
  // update and used in the where clause.
  def updateSQL[A](ignored: List[String] = List())(implicit ev: PgEntity[A]) = {
    val tablename = ev.tableName
    val columns = ev.columns.filterNot(c => c.isPk || ignored.contains(c.name))
    val updates = columns.map(_.updatePlaceHolder).mkString(", ")
    val pkClause = primaryKeys[A].map {
      case PgField(pk, _, _) =>
        s"$pk = {$pk}"
    }.mkString("", " and ", "")
    s"UPDATE $tablename SET $updates WHERE $pkClause"
  }

  // Body of a DELETE command
  // The fields used in the primary key are automatically used in the where
  // clause.
  def deleteSQL[A](implicit ev: PgEntity[A]) = {
    val tablename = ev.tableName
    val pkClause = primaryKeys[A].map {
      case PgField(pk, _, _) =>
        s"$pk = {$pk}"
    }.mkString("", " and ", "")
    s"DELETE FROM $tablename WHERE $pkClause"
  }
}
