import org.scalatest._

import java.util.UUID

import pgentity.pg_entity._
import pgentity.pg_entity.Entities._
import anorm.SqlStatementParser.parse
import magnolia._

object Values {
  case class DummyTable(dummyTableId: UUID, name: String, number: Int)

  implicit val DummyTablePgEntity = PgEntity.gen[DummyTable]

}

class PgEntitySpec extends FlatSpec with Matchers {
  "columns" should "contain a primary key" in {
    val cs = columns[Values.DummyTable]
    cs.filter(_.isPk) should be(
      List(PgField("dummy_table_id", Some("uuid"), true)))
  }
  "columnList" should "automatically prefix fields with the table name" in {
    val columns =
      columnList[Values.DummyTable](None).split(",").toList.map(_.trim)
    all(columns) should startWith(""""dummy_table".""")
  }
  "columnList" should "prefix fields with the given prefix" in {
    val prefix = "renamed_column_"
    val columns =
      columnList[Values.DummyTable](Some(prefix)).split(",").toList.map(_.trim)

    all(columns) should startWith("renamed_column_")
  }

  "INSERT statement" should "be exactly what I want" in {
    val statement = insertSQL[Values.DummyTable]
    println(parse(statement))

    statement should be(
      """insert into "dummy_table" (dummy_table_id,name,number) values ({dummy_table_id}::uuid,{name}::text,{number}::number)""")
  }

  "INSERT statement" should "include all fields" in {
    val statement = insertSQL[Values.DummyTable]
    val parsedPlaceholders = parse(statement).get.names

    parsedPlaceholders should contain theSameElementsAs (Values.DummyTablePgEntity.columns
      .map(_.name))
  }

  "UPDATE statement" should "include all fields" in {
    val statement = updateSQL[Values.DummyTable]()
    val parsedPlaceholders = parse(statement).get.names

    parsedPlaceholders should contain theSameElementsAs (Values.DummyTablePgEntity.columns
      .map(_.name))
  }
  "DELETE statement" should "include primary key" in {
    val statement = deleteSQL[Values.DummyTable]
    val parsedPlaceholders = parse(statement).get.names

    parsedPlaceholders should contain theSameElementsAs (primaryKeys[Values.DummyTable].map(_.name))
  }

  "tableName override" should "override table name" in {
    val name = implicitly[PgEntity[Values.DummyTable]].withTableName("yolo").tableName
    name should be("yolo")
  }

  "fields override" should "override fields" in {
    val fields = implicitly[PgEntity[Values.DummyTable]].withColumns(Nil).columns
    fields should be(Nil)
  }
}
