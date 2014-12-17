**Please note this is a work in progress, use it at your own risk**


# Anorm PG Entities

Anorm provides a great way to talk with a SQL database. It allows for great
liberty when writing SQL and provide rather nice ways of parsing results.
Anorm doesn't suffer from the issues common with ORMs: you're able to write
efficient SQL tailored to the database you use, and you're not forced to use a
poorly and automatically designed DB schema.

Basically, DB-agnostic ORMs prevent you from correctly using your SQL DB.
Anorm doesn't. It's not as type safe as other solutions, but it's a sweet spot
between safety and flexibility.

Unfortunately, anorm requires you to write quite a lot of boilerplate. Having
to type every single SQL query / command is bothersome and error-prone.

Anorm PG Entities is designed to free you from the boilerplate (provided you
use Postgresql, but you already are, aren't you?) by abstracting away the
field list (no more `SELECT *`) and `RowParser[A]`, thus allowing you to focus
on your queries structure, not minutiae. For the simple cases, Anorm PG
Entities generates all the SQL for you.

Unlike other tools, Anorm PG Entities separates your model from the mapping to
the database, which gives you flexibility. No need to put annotations
everywhere, you can override everything you want.

## Show me the code

For now you have to write the `PgEntity` instance yourself. Don't worry, it's
almost boilerplate-free.

Automagic instance derivation is in the works. Stay tuned.

### Regular use

```scala
case class DummyTable(
  id: UUID, // Please refrain from using sequential IDs :-)
  name: String,
  number: Int
)

implicit val DummyTablePgEntity = new PgEntity[DummyTable] {
  // The name of the table
  val tableName = "dummy_table"

  // The columns
  val columns = List(PgField("dummy_table_id", Some("UUID")), PgField("name"), PgField("number"))

  // The parser
  def parser(prefix: String) = {
    get[UUID](prefix + "dummy_table_id") ~
    str(prefix + "name") ~
    int(prefix + "number") map { case (id ~ name ~ number) => DummyTable(id, name, number) }
  }
}
```

### Automagic use

Work in progress

The idea is to use `shapeless.Generic` to automatically derive the `PgEntity`
instance from a case class.

## Build

    sbt compile

Please note that anorm PG Entities depends on
[scala-sql-parser](https://github.com/stephentu/scala-sql-parser)
**for the tests only**.

Scala-SQL-Parser is not released on a repo yet, you have to
`publish-local` it beforehand.
