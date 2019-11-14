
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "name.delafargue",
      scalaVersion := "2.12.6",
      crossScalaVersions := Seq("2.11.12","2.12.10","2.13.1"),
      version      := "0.1.1"
    )),
    name := "anorm-pg-entity",
    libraryDependencies ++= Seq(
      "org.playframework.anorm" %% "anorm" % "2.6.5",
      "org.scalaz" %% "scalaz-core" % "7.2.29",
      "com.chuusai" %% "shapeless" % "2.3.3",
      "org.specs2" %% "specs2-core" % "4.8.1" % Test,
      "org.specs2" %% "specs2-scalacheck" % "4.8.0" % Test
    ),
    scalacOptions ++= Seq(
      "-feature", "-unchecked", "-Xlint",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard", "-language:postfixOps"
    ),
    bintrayOrganization := Some("clevercloud"),
    bintrayRepository := "maven",
    licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
  )
