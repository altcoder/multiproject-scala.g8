import Dependencies._
import Version._

// ThisBuild settings take lower precedence,
// but can be shared across the multi projects.
def buildLevelSettings: Seq[Setting[_]] = Seq(
  organization in ThisBuild := "$organization$",
  version in ThisBuild      := $name$V
)

def commonSettings: Seq[Setting[_]] = Seq(
  // Scala Version
  scalaVersion := scalaV,
  crossScalaVersions := crossScalaV,

  // Publishing
  resolvers ++= resolutionRepos,
  publishArtifact in packageDoc := false,
  publishArtifact in Test := false,
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
  pomIncludeRepository := { _ => false },
  /** For open source project
  licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
  */
  homepage := Some(url("$url$")),
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
  ),
  
  javacOptions in compile ++= Seq(
    "-target", javaV, 
    "-source", javaV, 
    "-Xlint", "-Xlint:-serial"),
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-Yinline-warnings",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    "-language:higherKinds",
    "-language:postfixOps",
    "-language:existentials",
    "-feature"),
  testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-w", "1"),

  // Assembly settings
  test in assembly := {},
  assemblyMergeStrategy in assembly := {
    case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
    case "application.conf"                            => MergeStrategy.concat
    case "META-INF/MANIFEST.MF"                        => MergeStrategy.discard
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)  
  }
)

lazy val $name$Root: Project = (project in file(".")).
  aggregate(coreProj, sampleProj, benchmarkProj).
  settings(
    buildLevelSettings,
    commonSettings,
    rootSettings,
    publish := {},
    publishLocal := {}
  )
def rootSettings = Seq(
  initialCommands in console:=
  """
    import $organization$.core._
    import $organization$.sample._
    import $organization$.benchmark._
  """
)

/* ** subproject declarations ** */

lazy val coreProj = (project in file("core")).
  settings(
    commonSettings,
    name := "Core",
    libraryDependencies ++= Seq(
      scalatest   % Test,
      scalacheck  % Test
    )
  )

lazy val sampleProj = (project in file("sample")).
  dependsOn(coreProj).
  settings(
    commonSettings,
    name:= "Sample"
  )

lazy val benchmarkProj = (project in utilPath / "benchmark").
  dependsOn(coreProj).
  settings(
    minimalSettings,
    name := "Bemnchmark"
  )


/* Nested project paths */
def utilPath   = file("util")

