import sbt._

object Dependencies {
  val resolutionRepos = Seq(
    "Local Maven Repository"    at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    "NL4J Repository"           at "http://nativelibs4java.sourceforge.net/maven/",
    "Maven2 Dev Repository"     at "http://download.java.net/maven/2",
    "Typesafe Repository"       at "http://repo.typesafe.com/typesafe/releases/",
    "Spray Repository"          at "http://repo.spray.io/",
    "Sonatype Snapshots"        at "http://oss.sonatype.org/content/repositories/snapshots",
    "Scalaz Bintray Repository" at "https://dl.bintray.com/scalaz/releases"
  )

  val typesafeConfig = "com.typesafe"        % "config"           % "1.3.0"
  val logging       = "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
  val scalatest     = "org.scalatest"      %% "scalatest"   % "2.2.4" 
  val scalacheck    = "org.scalacheck"      %% "scalacheck"  % "1.12.3"
  val spark         = "org.apache.spark"    %% "spark-core" % "0.8.0-incubating" % "provided"
  val hadoop        = "org.apache.hadoop"   % "hadoop-client" % "2.0.0-cdh4.4.0" % "provided"
}

