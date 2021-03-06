package persistence

trait FeaturePersister {

  /**
   * Saves featureData
   * @param featureData
   * @param featureName name of the map features
   * @param dataFormat format of the featureData
   */
  def persist(featureData: String, featureName: String, dataFormat: String, dataSource: String): Unit

}

/**
 * FilePersister for writing the String data to the local file system
 * @param baseFolder
 */
class LocalFilePersister(baseFolder: String) extends FeaturePersister {

  override def persist(featureData: String, featureName: String, dataFormat: String, dataSource: String): Unit = {
    import java.io._
    val pw = new PrintWriter(new File(s"$baseFolder/${dataSource}_$featureName.$dataFormat"))
    pw.write(featureData)
    pw.close
  }
}

// put other persister classes here (or in separate files), like a cloud storage or database persister
