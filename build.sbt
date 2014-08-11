name := """sbt-entities"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "anorm" % "2.3.2",
  "org.specs2" %% "specs2" % "2.4" % "test"
)

scalacOptions ++= Seq(
  "-feature", "-unchecked", "-Xlint", "-Ywarn-inaccessible",
  "-Ywarn-nullary-override", "-Ywarn-nullary-unit", "-Ywarn-numeric-widen",
  "-Ywarn-value-discard", "-language:postfixOps"
)

scalacOptions in Test ++= Seq("-Yrangepos")
