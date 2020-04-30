package featurefetcher

import clientwrapper.OverpassWrapper
import constant.{DataSources, Features, OutputFormats}

class FeatureDownloader(overpassEndpoint: String) {
  val overpassWrapper = new OverpassWrapper(overpassEndpoint)
  // other client wrappers would be passed into the constructor

  /**
   * downloads the feature data and returns a case class containing the feature data as a String and
   * an enum describing the format of the data, which may be different from finalOutputFormat. It is
   * the responsibility of the caller to inspect the data format and perform any needed conversion.
   * @param dataSource
   * @param featureName
   * @param finalOutputFormat
   * @param boundaryCoordinates
   * @return
   */
  def downloadFeatureData(dataSource: String, featureName: String, finalOutputFormat: String, boundaryCoordinates: String): DataAndFormat = {
    if (dataSource == DataSources.osm.toString) {
      // information about Overpass Query Language:
      // https://wiki.openstreetmap.org/wiki/Overpass_API/Overpass_QL
      val querySkeleton =
        """
           |[out:%s];
           |(
           |%s
           |);
           |out body;
           |>;
           |out skel qt;
        """.stripMargin
      var queryOutputFormat: String = null
      val queryWithOutputFormat = OutputFormats.withName(finalOutputFormat) match {
          // in String.format() here, only the first %s is substituted (because the second one is substituted with %s
          // so that it can be substituted with overpass statements in subsequent steps
        case OutputFormats.xml => // create query for xml
          queryOutputFormat = OutputFormats.xml.toString
          String.format(querySkeleton, "xml", "%s")
        case OutputFormats.json => // create query for json
          queryOutputFormat = OutputFormats.json.toString
          String.format(querySkeleton, "json", "%s")
        case OutputFormats.geojson => // create query for xml
          // Overpass API can't return geojson, but there is a tool to convert the xml that it returns
          // to geojson. So xml is requested.
          queryOutputFormat = OutputFormats.xml.toString
          String.format(querySkeleton, "xml", "%s")
        case OutputFormats.csv => // create query for csv
          // more modifications are needed because getting csv output also requires specifying the output columms
          throw new NotImplementedError("csv format not yet supported")
        case _ => throw new IllegalArgumentException(s"requested output format $finalOutputFormat not recognized")
      }
      val query = insertQueryStatements(queryWithOutputFormat, featureName, boundaryCoordinates)
      // todo log the query instead of println
      println(query)
      val featureData = overpassWrapper.getFeatures(query)
      DataAndFormat(featureData, queryOutputFormat)
    } else {
      throw new IllegalArgumentException(s"data source $dataSource not recognized")
    }
  }

  /**
   *
   *
   * @param querySkeleton must have one %s which will be substituted with the overpass statements
   * @param featureName
   * @param boundaryCoordinates to be put into the overpass statements
   * @return
   */
  private def insertQueryStatements(querySkeleton: String, featureName: String, boundaryCoordinates: String): String = {
    Features.withName(featureName) match {
      case Features.trails => String.format(querySkeleton,
        s"""
           |way["highway"="path"]( poly: "$boundaryCoordinates");
           |way["highway"="footway"]( poly: "$boundaryCoordinates");
           |way["highway"="bridleway"]( poly: "$boundaryCoordinates");
           |way["highway"="steps"](poly: "$boundaryCoordinates");""".stripMargin)
      case Features.lakes => String.format(querySkeleton,
        s"""
           |relation["water"](poly: "$boundaryCoordinates");
           |way["water"](poly: "$boundaryCoordinates");
           |way["natural"="water"](poly: "$boundaryCoordinates");
       """.stripMargin)
      case Features.peaks => String.format(querySkeleton,
        s"""
           |node["natural"="peak"](poly: "$boundaryCoordinates");
           |node["natural"="volcano"](poly: "$boundaryCoordinates");
           |""".stripMargin)
      case Features.rivers => String.format(querySkeleton,
        s"""
           |relation["waterway"](poly: "$boundaryCoordinates");
           |way["waterway"](poly: "$boundaryCoordinates");
           |""".stripMargin)
      case Features.viewpoints => String.format(querySkeleton,
        s"""
           |node["tourism"="viewpoint"](poly: "$boundaryCoordinates");
           |""".stripMargin)
      case Features.tracks => String.format(querySkeleton,
        s"""
           |way["highway"="track"](poly: "$boundaryCoordinates");
           |""".stripMargin)
      case Features.roads => String.format(querySkeleton,
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
           |""".stripMargin)
      case _ => throw new IllegalArgumentException(s"feature $featureName not recognized")
    }
  }
}
