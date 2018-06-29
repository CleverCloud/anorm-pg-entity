lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "name.delafargue",
      scalaVersion := "2.12.6",
		crossScalaVersions := Seq("2.11.12","2.12.6"),
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "AnormPgEntity",
    libraryDependencies ++= Seq(
      "org.playframework.anorm" %% "anorm" % "2.6.2",
		"org.scalaz" %% "scalaz-core" % "7.2.25",
		"com.chuusai" %% "shapeless" % "2.3.3",
		"org.specs2" %% "specs2-core" % "4.3.0" % Test,
		"org.specs2" %% "specs2-scalacheck" % "4.3.0" % Test
    ),
    scalacOptions ++= Seq(
      "-feature", "-unchecked", "-Xlint", "-Ywarn-inaccessible",
      "-Ywarn-nullary-override", "-Ywarn-nullary-unit", "-Ywarn-numeric-widen",
      "-Ywarn-value-discard", "-language:postfixOps"
    )
  )
