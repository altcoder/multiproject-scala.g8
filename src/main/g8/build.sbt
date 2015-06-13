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

def minimalSettings: Seq[Setting[_]] =  commonSettings ++ customCommands 

/**
def baseSettings: Seq[Setting[_]] =
  minimalSettings ++ Seq(projectComponent) ++ baseScalacOptions ++ Licensed.settings ++ Formatting.settings

def testedBaseSettings: Seq[Setting[_]] =
  baseSettings ++ testDependencies
*/

lazy val $name$Root: Project = (project in file(".")).
  aggregate(nonRoots: _*).
  settings(
    buildLevelSettings,
    minimalSettings,
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
    minimalSettings,
    name := "Core",
    libraryDependencies ++= Seq(
      scalatest   % Test,
      scalacheck  % Test
    )
  )

lazy val sampleProj = (project in file("sample")).
  dependsOn(coreProj).
  settings(
    minimalSettings,
    name:= "Sample"
  )

lazy val benchmarkProj = (project in utilPath / "benchmark").
  dependsOn(coreProj).
  settings(
    minimalSettings,
    name := "Bemnchmark"
  )



lazy val myProvided = config("provided") intransitive
def allProjects = Seq(coreProj, sampleProj, benchmarkProj)
def projectsWithMyProvided = allProjects.map(p => p.copy(configurations = (p.configurations.filter(_ != Provided)) :+ myProvided))
lazy val nonRoots = projectsWithMyProvided.map(p => LocalProject(p.id))

/* Nested Projproject paths */
def corePath   = file("core")
def utilPath   = file("util")
def samplePath = file("sample")


lazy val safeUnitTests = taskKey[Unit]("Known working tests (for both 2.10 and 2.11)")
lazy val safeProjects: ScopeFilter = ScopeFilter(
  inProjects(coreProj, sampleProj, benchmarkProj),
  inConfigurations(Test)
)

def customCommands: Seq[Setting[_]] = Seq(
  commands += Command.command("setupBuildScala211") { state =>
    s"""set scalaVersion in ThisBuild := "$scala211" """ ::
      state
  },
  // This will be invoked by Travis
  commands += Command.command("checkBuildScala211") { state =>
    s"++ $scala211" ::
      // First compile everything before attempting to test
      "all compile test:compile" ::
      // Now run known working tests.
      safeUnitTests.key.label ::
      state
  },
  safeUnitTests := {
    test.all(safeProjects).value
  },
  commands += Command.command("release-local") { state =>
    "clean" ::
    "allPrecompiled/clean" ::
    "allPrecompiled/compile" ::
    "allPrecompiled/publishLocal" ::
    "so compile" ::
    "so publishLocal" ::
    "reload" ::
    state
  },
  commands += Command.command("release-public") { state =>
    // TODO - Any sort of validation
    "checkCredentials" ::
    "clean" ::
    "allPrecompiled/clean" ::
      "allPrecompiled/compile" ::
      "allPrecompiled/publishSigned" ::
      "conscript-configs" ::
      "so compile" ::
      "so publishSigned" ::
      "bundledLauncherProj/publishLauncher" ::
      state
  },
  commands += Command.command("release-nightly") { state =>
    "stamp-version" ::
      "clean" ::
      "allPrecompiled/clean" ::
      "allPrecompiled/compile" ::
      "allPrecompiled/publish" ::
      "compile" ::
      "publish" ::
      "bintrayRelease" ::
      state
  }
)
