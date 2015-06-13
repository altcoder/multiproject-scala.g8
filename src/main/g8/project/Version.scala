/**
 * Library versions
 */
object Version {
  val $name$  = "$version$"

  /* Even though we support cross-build to 2.11 the default target is scala 2.10 primarily because Cloudera
   * (and likely others) spark distributions target 2.10 in their default spark-assembly.jar.
   * One can envoke the cross-build to 2.11 by prefixing command with '+' (ex: + assembly)
   * Until the deployment of spark on 2.11 is fully addressed we are going to target 2.10 to minimize confusion.
   */
  val scala       = "2.10.4"
  lazy val hadoop      = Environment.hadoopVersion
  lazy val spark       = Environment.sparkVersion
}
