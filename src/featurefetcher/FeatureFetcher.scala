package featurefetcher

import clientwrapper.MapFeatureClientWrapper
import converter.FormatConverter

trait FeatureFetcher {
  def fetchFeatures(): String

  def insertQuery(subQuery: String): String = {
    s"""
          |[out:xml];
          |(
          |$subQuery
          |);
          |out body;
          |>;
          |out skel qt;
     """.stripMargin
  }
}

// companion object
object FeatureFetcherFactory {
  def apply(featureName: String,
            boundaryCoordinates: String,
            outputFormat: String,
            overpassWrapper: MapFeatureClientWrapper,
            converter: FormatConverter): FeatureFetcher = {
    featureName match {
      case "rivers" => new RiverFetcher(boundaryCoordinates, outputFormat, overpassWrapper, converter)
      case "peaks" => new PeakFetcher(boundaryCoordinates, outputFormat, overpassWrapper, converter)
      case "lakes" => new LakeFetcher(boundaryCoordinates, outputFormat, overpassWrapper, converter)
    }
  }
}

class RiverFetcher(val boundaryCoordinates: String,
                   val outputFormat: String,
                   val overpassWrapper: MapFeatureClientWrapper,
                   val converter: FormatConverter) extends FeatureFetcher {
  val query =
    s"""
      |relation["waterway"](poly: "$boundaryCoordinates");
      |way["waterway"](poly: "$boundaryCoordinates");
      |""".stripMargin

  def fetchFeatures(): String = {
    val fullQuery = insertQuery(query)
    println(fullQuery)
    overpassWrapper.getFeatures(fullQuery)
  }
}

class LakeFetcher(boundaryCoordinates: String,
                  outputFormat: String,
                  overpassWrapper: MapFeatureClientWrapper,
                  converter: FormatConverter) extends FeatureFetcher {
  def fetchFeatures(): String = {
    ""
  }
}

class PeakFetcher(boundaryCoordinates: String,
                  outputFormat: String,
                  overpassWrapper: MapFeatureClientWrapper,
                  converter: FormatConverter) extends FeatureFetcher {
  def fetchFeatures(): String = {
    ""
  }
}


class TrailFetcher(boundaryCoordinates: String,
                  outputFormat: String,
                  overpassWrapper: MapFeatureClientWrapper,
                  converter: FormatConverter) extends FeatureFetcher {
  val query = s"""
                |way["highway"="path"]( poly: "$boundaryCoordinates");
                |way["highway"="footway"]( poly: "$boundaryCoordinates");
                |way["highway"="bridleway"]( poly: "$boundaryCoordinates");
                |way["highway"="steps"](poly: "$boundaryCoordinates");""".stripMargin
  def fetchFeatures(): String = {
    ""
  }
}
