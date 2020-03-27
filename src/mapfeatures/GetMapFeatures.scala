package mapfeatures

import featurefetcher.FeatureFetcher2
import org.apache.commons.cli.{CommandLine, DefaultParser, Option, Options, ParseException}

import scala.io.Source

object GetMapFeatures {

  val BoundaryCoordinatesFile = "boundary_coordinates_file"
  val OutputFolder = "output_folder"
  val Features = "features"
  val OutputFormat = "output_format"
  val OverpassEndpoint = "overpass_endpoint"
  val OsmXmlToGeoJsonToolPath = "osm_xml_to_geo_json_tool_path"
  val FetchableFeatures = List("trails", "lakes", "peaks", "viewpoints", "tracks", "rivers", "roads")

  def main(args: Array[String]): Unit = {
    // parse args
    // read polygon
    // setup wrapper(s) for downloading data
    // create a MapFeatureFetchers for each feature requested in args
    // iterate over each calling fetchFeatures, putting the String (or byte[]?) results in a map from
    //     feature name to data
    // persist the features
    val commandLine = parseArgs(args)
    // todo validate args
    val boundaryCoordinates = parseBoundaryCoordinates(commandLine.getOptionValue(BoundaryCoordinatesFile))
    val outputFolder = commandLine.getOptionValue(OutputFolder)
    val features = commandLine.getOptionValue(Features)
    val outputFormat = commandLine.getOptionValue(OutputFormat)
    val overpassEndpoint = commandLine.getOptionValue(OverpassEndpoint)
    val osmXmlToGeoJsonToolPath = commandLine.getOptionValue(OsmXmlToGeoJsonToolPath)

    val featureNames = features.split("\\s+")

    val featureFetcher = new FeatureFetcher2
    featureNames.foreach(
      feature => featureFetcher.fetchFeatures("osm", feature, outputFormat,
                overpassEndpoint, boundaryCoordinates, outputFolder, osmXmlToGeoJsonToolPath))


   /*
    case class FeatureNameAndFetcher(name: String, fetcher: FeatureFetcher)
    val featureNamesAndFetchers = featureNames.map(x =>
      FeatureNameAndFetcher(x, FeatureFetcherFactory.apply(x, boundaryCoordinates, outputFormat, wrapper, null)))
    case class FeatureNameAndData(name: String, data: String)
    val featureNamesAndXmlData = featureNamesAndFetchers.map(featureFetcher => FeatureNameAndData(featureFetcher.name, featureFetcher.fetcher.fetchFeatures()))
    val formatConverter = new OsmXmlToGeoJsonConverter(osmXmlToGeoJsonToolPath)
    val featureNamesAndGeojsonData = featureNamesAndXmlData.map(x => FeatureNameAndData(x.name, formatConverter.convert(x.data)))
    val featurePersister = new LocalFilePersister(outputFolder, outputFormat)
    featureNamesAndGeojsonData.foreach(x => featurePersister.persist(x.name, x.data ))
    */
  }

  private def parseBoundaryCoordinates(boundaryCoordinatesFile: String): String = {
    val source = Source.fromFile(boundaryCoordinatesFile)
    val coordinates = source.mkString.trim
    source.close()
    coordinates
  }

  def parseArgs(args: Array[String]): CommandLine = {
    val options = new Options
    options.addOption(new Option(BoundaryCoordinatesFile, BoundaryCoordinatesFile, true, "path to file containing coordinates of boundary"))
    options.addOption(new Option(OutputFolder, OutputFolder, true, "path to directory into which results should be written"))
    options.addOption(new Option(Features, Features, true, "list of one or more specific features to download. If empty or omitted, all features are downloaded"))
    options.addOption(new Option(OutputFormat, OutputFormat, true, "output format, either osmxml or geojson"))
    options.addOption(new Option(OverpassEndpoint, OverpassEndpoint, true, "endpoint for the overpass API"))
    options.addOption(new Option(OsmXmlToGeoJsonToolPath, OsmXmlToGeoJsonToolPath, true, "path to nodejs tool that converts osm xml to geojson. only required if conversion to geojson is needed"))
    try (new DefaultParser).parse(options, args)
    catch {
      case e: ParseException =>
        throw new IllegalArgumentException(e.getMessage)
    }
  }
}