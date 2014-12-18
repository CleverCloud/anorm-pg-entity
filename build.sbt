name := """sbt-entities"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "anorm" % "2.3.2",
  "org.specs2" %% "specs2" % "2.4" % "test",
  "org.scalaz" %% "scalaz-core" % "7.1.0",
  "com.chuusai" %% "shapeless" % "2.0.0",
  "default" % "scala-sql-parser_2.10" % "0.1-SNAPSHOT" % Test
)

scalacOptions ++= Seq(
  "-feature", "-unchecked", "-Xlint", "-Ywarn-inaccessible",
  "-Ywarn-nullary-override", "-Ywarn-nullary-unit", "-Ywarn-numeric-widen",
  "-Ywarn-value-discard", "-language:postfixOps"
)

scalacOptions in Test ++= Seq("-Yrangepos")

scalariformSettings
