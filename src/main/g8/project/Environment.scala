import scala.util.Properties

/**
 * Load environment variables into SBT (could be versions or any other values)
 */
object Environment {
  def either(environmentVariable: String, default: String): String =
    Properties.envOrElse(environmentVariable, default)

  lazy val hadoopVersion  = either("SPARK_HADOOP_VERSION", "2.5.0")
  lazy val sparkVersion   = either("SPARK_VERSION", "1.2.0")
}
