package featurefetcher

import clientwrapper.MapFeatureClientWrapper
import converter.FormatConverter

trait FeatureFetcher {
  def fetchFeatures(): String
}


trait OsmFeatureFetcher extends FeatureFetcher {
  def fetchFeatures(): String

  def generateQuery(subQuery: String): String = {
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

class TrailFetcher(boundaryCoordinates: String,
                   outputFormat: String,
                   overpassWrapper: MapFeatureClientWrapper,
                   converter: FormatConverter) extends OsmFeatureFetcher {
  val query = s"""
                 |way["highway"="path"]( poly: "$boundaryCoordinates");
                 |way["highway"="footway"]( poly: "$boundaryCoordinates");
                 |way["highway"="bridleway"]( poly: "$boundaryCoordinates");
                 |way["highway"="steps"](poly: "$boundaryCoordinates");""".stripMargin

  override def fetchFeatures(): String = {
    val fullQuery = generateQuery(query)
    println(fullQuery)
    overpassWrapper.getFeatures(fullQuery)
  }
}

class LakeFetcher(boundaryCoordinates: String,
                  outputFormat: String,
                  overpassWrapper: MapFeatureClientWrapper,
                  converter: FormatConverter) extends OsmFeatureFetcher {
  val query =
    s"""
       |relation["water"](poly: "$boundaryCoordinates");
       |way["water"](poly: "$boundaryCoordinates");
       |way["natural"="water"](poly: "$boundaryCoordinates");
       """.stripMargin

  override def fetchFeatures(): String = {
    val fullQuery = generateQuery(query)
    println(fullQuery)
    overpassWrapper.getFeatures(fullQuery)
  }
}

class PeakFetcher(boundaryCoordinates: String,
                  outputFormat: String,
                  overpassWrapper: MapFeatureClientWrapper,
                  converter: FormatConverter) extends OsmFeatureFetcher {
  val query = s"""
     |node["natural"="peak"](poly: "$boundaryCoordinates");
     |node["natural"="volcano"](poly: "$boundaryCoordinates");
     |""".stripMargin

  override def fetchFeatures(): String = {
    val fullQuery = generateQuery(query)
    println(fullQuery)
    overpassWrapper.getFeatures(fullQuery)
  }
}

class ViewpointFetcher(boundaryCoordinates: String,
                  outputFormat: String,
                  overpassWrapper: MapFeatureClientWrapper,
                  converter: FormatConverter) extends OsmFeatureFetcher {
  val query = s"""
                 |node["tourism"="viewpoint"](poly: "$boundaryCoordinates");
                 |""".stripMargin

  override def fetchFeatures(): String = {
    val fullQuery = generateQuery(query)
    println(fullQuery)
    overpassWrapper.getFeatures(fullQuery)
  }
}

class TrackFetcher(boundaryCoordinates: String,
                       outputFormat: String,
                       overpassWrapper: MapFeatureClientWrapper,
                       converter: FormatConverter) extends OsmFeatureFetcher {
  val query = s"""
                 |way["highway"="track"](poly: "$boundaryCoordinates");
                 |""".stripMargin

  override def fetchFeatures(): String = {
    val fullQuery = generateQuery(query)
    println(fullQuery)
    overpassWrapper.getFeatures(fullQuery)
  }
}

class RiverFetcher(val boundaryCoordinates: String,
                   val outputFormat: String,
                   val overpassWrapper: MapFeatureClientWrapper,
                   val converter: FormatConverter) extends OsmFeatureFetcher {
  val query =
    s"""
       |relation["waterway"](poly: "$boundaryCoordinates");
       |way["waterway"](poly: "$boundaryCoordinates");
       |""".stripMargin

  override def fetchFeatures(): String = {
    val fullQuery = generateQuery(query)
    println(fullQuery)
    overpassWrapper.getFeatures(fullQuery)
  }
}

class RoadFetcher(val boundaryCoordinates: String,
                   val outputFormat: String,
                   val overpassWrapper: MapFeatureClientWrapper,
                   val converter: FormatConverter) extends OsmFeatureFetcher {
  val query =
    s"""
       |way["highway"="motorway"]( poly: "$boundaryCoordinates");
       |way["highway"="trunk"]( poly: "$boundaryCoordinates");
       |way["highway"="primary"]( poly: "$boundaryCoordinates");
       |way["highway"="secondary"]( poly: "$boundaryCoordinates");
       |way["highway"="tertiary"]( poly: "$boundaryCoordinates");
       |way["highway"="unclassified"]( poly: "$boundaryCoordinates");
       |way["highway"="motorway_link"]( poly: "$boundaryCoordinates");
       |way["highway"="trunk_link"]( poly: "$boundaryCoordinates");
       |way["highway"="primary_link"]( poly: "$boundaryCoordinates");
       |way["highway"="secondary_link"]( poly: "$boundaryCoordinates");
       |way["highway"="tertiary_link"]( poly: "$boundaryCoordinates");
       |way["highway"="living_street"]( poly: "$boundaryCoordinates");
       |way["highway"="service"]( poly: "$boundaryCoordinates");
       |way["highway"="road"]( poly: "$boundaryCoordinates");
       |""".stripMargin

  override def fetchFeatures(): String = {
    val fullQuery = generateQuery(query)
    println(fullQuery)
    overpassWrapper.getFeatures(fullQuery)
  }
}
// companion object
object FeatureFetcherFactory {
  // todo should all these args except the feature name be part of the constructor?
  def apply(featureName: String,
            boundaryCoordinates: String,
            outputFormat: String,
            overpassWrapper: MapFeatureClientWrapper,
            converter: FormatConverter): FeatureFetcher = {

    featureName match {
      case "trails" => new TrailFetcher(boundaryCoordinates, outputFormat, overpassWrapper, converter)
      case "lakes" => new LakeFetcher(boundaryCoordinates, outputFormat, overpassWrapper, converter)
      case "peaks" => new PeakFetcher(boundaryCoordinates, outputFormat, overpassWrapper, converter)
      case "viewpoints" => new ViewpointFetcher(boundaryCoordinates, outputFormat, overpassWrapper, converter)
      case "tracks" => new TrackFetcher(boundaryCoordinates, outputFormat, overpassWrapper, converter)
      case "rivers" => new RiverFetcher(boundaryCoordinates, outputFormat, overpassWrapper, converter)
      case "roads" => new RoadFetcher(boundaryCoordinates, outputFormat, overpassWrapper, converter)
    }
  }
}
