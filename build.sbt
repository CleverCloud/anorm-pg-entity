
val ARTIFACTORY_ADDON_HOST = sys.env.get("ARTIFACTORY_ADDON_HOST").getOrElse(throw new RuntimeException("Environment variable ARTIFACTORY_ADDON_HOST missing"))
val ARTIFACTORY_SBT_RELEASE_REPOSITORY = sys.env.get("ARTIFACTORY_SBT_RELEASE_REPOSITORY").getOrElse(throw new RuntimeException("Environment variable ARTIFACTORY_SBT_RELEASE_REPOSITORY missing"))
val ARTIFACTORY_SBT_RELEASE_USER = sys.env.get("ARTIFACTORY_SBT_RELEASE_USER").getOrElse(throw new RuntimeException("Environment variable ARTIFACTORY_SBT_RELEASE_USER missing"))
val ARTIFACTORY_SBT_RELEASE_PASSWORD = sys.env.get("ARTIFACTORY_SBT_RELEASE_PASSWORD").getOrElse(throw new RuntimeException("Environment variable ARTIFACTORY_SBT_RELEASE_PASSWORD missing"))

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "name.delafargue",
      scalaVersion := "2.12.6",
		crossScalaVersions := Seq("2.11.12","2.12.6"),
      version      := "0.1.0"
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
    ),
    publishTo := Some("Artifactory Realm" at "https://" + ARTIFACTORY_ADDON_HOST + "/" + ARTIFACTORY_SBT_RELEASE_REPOSITORY),
    credentials += Credentials("Artifactory Realm", ARTIFACTORY_ADDON_HOST, ARTIFACTORY_SBT_RELEASE_USER, ARTIFACTORY_SBT_RELEASE_PASSWORD),
    bintrayOrganization := Some("clevercloud"),
    bintrayRepository := "maven",
    licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
  )
