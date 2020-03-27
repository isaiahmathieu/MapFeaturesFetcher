package featurefetcher

import clientwrapper.OverpassWrapper

class FeatureDownloader2(overpassEndpoint: String) {
  val overpassWrapper = new OverpassWrapper(overpassEndpoint)
  // other client wrappers would be passed into the constructor

  /**
   * downloads the feature data and returns a case class containing the feature data as a String and
   * an enum describing the format of the data
   * @param dataSource
   * @param featureName
   * @param finalOutputFormat
   * @param boundaryCoordinates
   * @return
   */
  def downloadFeatureData(dataSource: String, featureName: String, finalOutputFormat: String, boundaryCoordinates: String): DataAndFormat = {
    if ("osm".equals(dataSource)) {
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
      val queryWithOutputFormat = finalOutputFormat match {
          // in String.format() here, only the first %s is substituted (because the second one is substituted with %s
          // so that it can be substituted with overpass statements in subsequent steps
        case "xml" => // create query for xml
          queryOutputFormat = "xml"
          String.format(querySkeleton, "xml", "%s")
        case "json" => // create query for json
          queryOutputFormat = "json"
          String.format(querySkeleton, "json", "%s")
        case "geojson" => // create query for xml
          queryOutputFormat = "xml"
          String.format(querySkeleton, "xml", "%s")
        case "csv" => // create query for csv
          // more modifications are needed because getting csv output also requires specifying the output columms
          throw new NotImplementedError("csv format not yet supported")
        case _ => throw new IllegalArgumentException(s"requested output format $finalOutputFormat not recognized")
      }
      val query = insertQueryStatements(queryWithOutputFormat, featureName, boundaryCoordinates)
      val featureData = overpassWrapper.getFeatures(query)
      DataAndFormat(featureData, queryOutputFormat)
    } else {
      null
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
    featureName match {
      case "rivers" => String.format(querySkeleton,
          s"""
             |relation["waterway"](poly: "$boundaryCoordinates");
             |way["waterway"](poly: "$boundaryCoordinates");
             |""".stripMargin)
      case "trails" => String.format(querySkeleton,
        s"""
           |way["highway"="path"]( poly: "$boundaryCoordinates");
           |way["highway"="footway"]( poly: "$boundaryCoordinates");
           |way["highway"="bridleway"]( poly: "$boundaryCoordinates");
           |way["highway"="steps"](poly: "$boundaryCoordinates");""".stripMargin)
      case "lakes" => String.format(querySkeleton,
        s"""
           |relation["water"](poly: "$boundaryCoordinates");
           |way["water"](poly: "$boundaryCoordinates");
           |way["natural"="water"](poly: "$boundaryCoordinates");
       """.stripMargin)
      case "peaks" => String.format(querySkeleton,
        s"""
           |node["natural"="peak"](poly: "$boundaryCoordinates");
           |node["natural"="volcano"](poly: "$boundaryCoordinates");
           |""".stripMargin)
      case "viewpoints" => String.format(querySkeleton,
        s"""
           |node["tourism"="viewpoint"](poly: "$boundaryCoordinates");
           |""".stripMargin)
      case "tracks" => String.format(querySkeleton,
        s"""
           |way["highway"="track"](poly: "$boundaryCoordinates");
           |""".stripMargin)
      case "roads" => String.format(querySkeleton,
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
