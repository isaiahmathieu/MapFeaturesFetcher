package persistence

trait FeaturePersister {

def persist(featureName: String, featureData: String)

}

class LocalFilePersister(baseFolder: String, outputFormat: String) extends FeaturePersister {
  def persist(featureName: String, featureData: String) = {
    import java.io._
    val pw = new PrintWriter(new File(s"$baseFolder/$featureName.$outputFormat"))
    pw.write(featureData)
    pw.close
  }
}

class LocalFilePersister2 {

  def persist(baseFolder: String, featureData: String, featureName: String, format: String): Unit = {
    import java.io._
    val pw = new PrintWriter(new File(s"$baseFolder/$featureName.$format"))
    pw.write(featureData)
    pw.close
  }

}

// put other persisters here:
