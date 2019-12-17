// Generated with scalagen

val scalatestVersion = "3.1.0"

val commonSettings = Seq(
  scalaVersion := "2.12.8",
  version := "0.2.4",
  organization := "io.mwielocha",
  scalacOptions ++= Seq(
    "-language:postfixOps",
    "-language:implicitConversions",
    "-deprecation",
    "-feature",
    "-Yrangepos",
    "-Ywarn-unused-import",
    "-language:existentials",
    "-language:higherKinds",
    "-Ypartial-unification",
    "-language:postfixOps",
  ),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  homepage := Some(url("https://github.com/mwielocha/differential")),
  scmInfo := Some(
  ScmInfo(url("https://github.com/mwielocha/differential"),
    "git@github.com:mwielocha/differential.git")),
  developers := List(
    Developer("mwielocha",
      "Mikolaj Wielocha",
      "mwielocha@icloud.com",
      url("https://github.com/mwielocha"
      )
    )
  ),
  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
  publishMavenStyle := true,
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging
  )
)

lazy val `differential-core` = (project in file("differential-core")).
  settings(commonSettings ++ Seq(
    name := "differential-core",
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.3",
      "org.scalatest" %% "scalatest" % scalatestVersion % Test,
    )
  ))

lazy val `differential-scalatest` = (project in file("differential-scalatest")).
  settings(commonSettings ++ Seq(
    name := "differential-scalatest",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalatestVersion,
      "com.lihaoyi" %% "pprint" % "0.5.6"
    )
  )).dependsOn(`differential-core`)

lazy val root = (project in file("."))
  .aggregate(`differential-core`, `differential-scalatest`)


