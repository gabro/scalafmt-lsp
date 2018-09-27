inThisBuild(
  List(
    version ~= { dynVer =>
      if (sys.env.contains("CI")) dynVer
      else "SNAPSHOT" // only for local publishng
    },
    scalaVersion := V.scala212,
    crossScalaVersions := List(V.scala212),
    scalacOptions ++= List(
      "-Yrangepos",
      "-deprecation",
      "-Ywarn-unused-import"
    ),
    organization := "org.scalameta",
    licenses := Seq(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    testFrameworks := new TestFramework("utest.runner.Framework") :: Nil,
    libraryDependencies += "com.lihaoyi" %% "utest" % "0.6.0" % Test,
    homepage := Some(url("https://github.com/gabro/scalafmt-lsp")),
    developers := List(
      Developer(
        "gabro",
        "Gabriele Petronella",
        "gabriele@buildo.io",
        url("https://github.com/gabro")
      )
    ),
    scmInfo in ThisBuild := Some(
      ScmInfo(
        url("https://github.com/gabro/scalafmt-lsp"),
        s"git@github.com:gabro/scalafmt-lsp.git"
      )
    )
  )
)

lazy val V = new {
  val scala212 = "2.12.6"
  val scalafmt = "1.6.0-RC4"
}

lazy val noPublish = List(
  publishTo := None,
  publishArtifact := false,
  skip in publish := true
)

lazy val scalafmtLsp = project
  .in(file("scalafmt-lsp"))
  .settings(
    resolvers += Resolver.sonatypeRepo("releases"),
    libraryDependencies ++= List(
      "org.scalameta" %% "lsp4s" % "0.2.0",
      "org.scalameta" %% "scalameta" % "4.0.0",
      "com.geirsson" %% "scalafmt-core" % "1.6.0-RC4"
    )
  )
