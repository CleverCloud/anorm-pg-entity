import org.specs2._

import java.util.UUID

import pgentity.pg_entity._
import anorm._
import anorm.SqlParser._
import anorm.SqlStatementParser.parse

object Values {
  case class DummyTable(
    id: UUID,
    name: String,
    number: Int)

  implicit val DummyTablePgEntity = new PgEntity[DummyTable] {
    val tableName = "dummy_table"
    val columns = List(PgField("dummy_table_id", Some("UUID")), PgField("name"), PgField("number"))
    def parser(prefix: String) = {
      get[UUID](prefix + "dummy_table_id") ~
        str(prefix + "name") ~
        int(prefix + "number") map { case (id ~ name ~ number) => DummyTable(id, name, number) }
    }
  }
}

class PgEntitySpec extends mutable.Specification with ScalaCheck {
  "columnList" should {
    "automatically prefix fields with the table name" in {
      val columns = columnList[Values.DummyTable](None).split(",").toList.map(_.trim)

      columns.forall({ c =>
        c must startWith("dummy_table.")
      })
    }

    "prefix fields with the given prefix" in {
      val prefix = "renamed_column_"
      val columns = columnList[Values.DummyTable](Some(prefix)).split(",").toList.map(_.trim)

      columns.forall({ c =>
        c must startWith("renamed_column_")
      })
    }

    "include all fields in insert statement" in {
      val statement = insertSQL[Values.DummyTable]
      val (_, parsedPlaceholders) = parse(statement)

      parsedPlaceholders must containTheSameElementsAs(Values.DummyTablePgEntity.columns.map(_.name))
    }

    "include all fields in update statement" in {
      val statement = updateSQL[Values.DummyTable]()
      val (_, parsedPlaceholders) = parse(statement)

      parsedPlaceholders must containTheSameElementsAs(Values.DummyTablePgEntity.columns.map(_.name))
    }
  }
}
