lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "name.delafargue",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "AnormPgEntity",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "anorm" % "2.6.0-M1",
      "com.propensive" %% "magnolia" % "0.5.0",
      "org.scalatest" %% "scalatest" % "3.0.3" % Test
    ),
    scalacOptions ++= Seq(
      "-feature", "-unchecked", "-Xlint", "-Ywarn-inaccessible",
      "-Ywarn-nullary-override", "-Ywarn-nullary-unit", "-Ywarn-numeric-widen",
      "-Ywarn-value-discard", "-language:postfixOps"
    )
  )


