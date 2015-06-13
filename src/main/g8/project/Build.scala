import sbt._
import sbt.Keys._

// sbt-assembly
import sbtassembly.Plugin._
import AssemblyKeys._

object Info {
  val name = "$name$"
  val organization = "$organization$"
  val description = "$desc$"
  val url = "$url$"
}

object $name;format="Camel"$Build extends Build {
  import Dependencies._

  // Default settings
  override lazy val settings = super.settings ++
  Seq(
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " },
    version := Version.$name$,
    scalaVersion := Version.scala,
    crossScalaVersions := Seq("2.11.5", "2.10.5"),
    organization := Info.organization,

    // disable annoying warnings about 2.10.x
    conflictWarning in ThisBuild := ConflictWarning.disable,
    scalacOptions ++=
      Seq("-deprecation",
        "-unchecked",
        "-Yinline-warnings",
        "-language:implicitConversions",
        "-language:reflectiveCalls",
        "-language:higherKinds",
        "-language:postfixOps",
        "-language:existentials",
        "-feature"),
      publishMavenStyle := true,
      publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository"))),
      /** For open source project
        publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value)
          Some("snapshots" at nexus + "content/repositories/snapshots") 
        else
          Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      }
      */
      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false },
      /** For open source project
      licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
      */
      homepage := Some(url(Info.url)),
      pomExtra := (
        <scm>
          <url>git@$gitrepo$.com:$name$/$name$.git</url>
          <connection>scm:git:git@$gitrepo$:$name$/$name$.git</connection>
          </scm>
          <developers>
            <developer>
              <id>jafaeldon</id>
              <name>James Faeldon</name>
              <url>http://$gitrepo$/jafaeldon/</url>
            </developer>
          </developers>
      )
  )

  val defaultAssemblySettings = assemblySettings ++
  Seq(
    test in assembly := {},
    mergeStrategy in assembly <<= (mergeStrategy in assembly) {
      (old) => {
        case "reference.conf" => MergeStrategy.concat
        case "application.conf" => MergeStrategy.concat
        case "META-INF/MANIFEST.MF" => MergeStrategy.discard
        case _ => MergeStrategy.first
      }
    },
    resolvers ++= resolutionRepos
  )

  // Project: root
  lazy val root = Project(Info.name, file("."))
    .aggregate(core, sample, benchmark)
    .settings(
      initialCommands in console:=
      """
        import $organization$.core._
        import $organization$.sample._
        import $organization$.benchmark._
      """
    )

  // Project: core
  lazy val core = Project("core", file("core"))
    .settings(coreSettings: _*)
  lazy val coreSettings = Seq(
    name := "$name$-core",
    libraryDependencies ++= Seq(
      scalatest   % Test,
      scalacheck  % Test
    )
  ) ++ defaultAssemblySettings

  // Project: sample
  lazy val sample = Project("sample", file("sample"))
    .dependsOn(core)
    .settings(sampleSettings: _*)
  lazy val sampleSettings = Seq(
    name := "$name$-sample",
    libraryDependencies ++= Seq(
      scalatest   % Test,
      scalacheck  % Test
    )
  ) ++ defaultAssemblySettings


  // Project: benchmark
  lazy val benchmark = Project("benchmark", file("benchmark"))
    .dependsOn(core)
    .dependsOn(sample)
    .settings(benchmarkSettings: _*)
  lazy val benchmarkSettings = Seq(
    name := "$name$-benchmark",
    parallelExecution := false,
    fork in test := false,
    javaOptions in run += "-Xmx2G",
    scalacOptions in compile ++= Seq("-optimize"),
    libraryDependencies ++= Seq(
      scalatest   % Test,
      scalacheck  % Test
    )
  ) ++ defaultAssemblySettings

}

