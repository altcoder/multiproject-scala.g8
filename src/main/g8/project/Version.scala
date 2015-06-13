/**
 * Library versions
 */
object Version {
  lazy val scala210 = "2.10.5"
  lazy val scala211 = "2.11.5"

  val $name$V  = "$version$"

  val javaV       = "7"
  val scalaV      = scala210
  val crossScalaV = Seq(scala210, scala211)
  lazy val hadoopV      = Environment.hadoopVersion
  lazy val sparkV       = Environment.sparkVersion

}
