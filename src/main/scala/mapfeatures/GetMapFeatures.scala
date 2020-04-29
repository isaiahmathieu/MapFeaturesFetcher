package mapfeatures

import featurefetcher.FeatureFetcher
import org.apache.commons.cli
import org.apache.commons.cli.{CommandLine, DefaultParser, HelpFormatter, Option, Options, ParseException}

import scala.io.Source

object GetMapFeatures {

  val BoundaryCoordinatesFile = "boundary_coordinates_file"
  val OutputFolder = "output_folder"
  val Features = "features"
  val OutputFormat = "output_format"
  val OverpassEndpoint = "overpass_endpoint"
  val OsmXmlToGeoJsonToolPath = "osm_xml_to_geo_json_tool_path"
  val DataSource = "data_source"
  //var options: Options = _

  def main(args: Array[String]): Unit = {
    val commandLine = parseArgs(args)
    // todo validate args
    val boundaryCoordinates = parseBoundaryCoordinates(commandLine.getOptionValue(BoundaryCoordinatesFile))
    val outputFolder = commandLine.getOptionValue(OutputFolder)
    val features = commandLine.getOptionValue(Features)
    val outputFormat = commandLine.getOptionValue(OutputFormat)
    val overpassEndpoint = commandLine.getOptionValue(OverpassEndpoint)
    val osmXmlToGeoJsonToolPath = commandLine.getOptionValue(OsmXmlToGeoJsonToolPath)
    val dataSource = commandLine.getOptionValue(DataSource)

    val featureNames = features.split("\\s+")

    val featureFetcher = new FeatureFetcher
    featureNames.foreach(
      feature => featureFetcher.fetchFeatures(dataSource, feature, outputFormat,
                overpassEndpoint, boundaryCoordinates, outputFolder, osmXmlToGeoJsonToolPath))
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
    options.addOption(new Option(OutputFolder, OutputFolder, true, "path to folder into which results should be written"))
    options.addOption(new Option(Features, Features, true, s"list of one or more specific features to download. each feature type should be separated by a space. possible values are: ${constant.Features.values.map(x => x.toString).mkString(",")}"))
    options.addOption(new Option(OutputFormat, OutputFormat, true, "output format, either xml, json, or geojson"))
    options.addOption(new Option(OverpassEndpoint, OverpassEndpoint, true, "endpoint for the Overpass API"))
    options.addOption(new Option(OsmXmlToGeoJsonToolPath, OsmXmlToGeoJsonToolPath, true, "path to nodejs tool that converts osm xml to geojson. only required if conversion to geojson is needed"))
    options.addOption(new Option(DataSource, DataSource, true, "data source, currently the only valid argument for this is 'osm' (Open Street Maps)"))
    try {
      (new DefaultParser).parse(options, args)
    } catch {
      case e: Exception =>
        (new HelpFormatter).printHelp(99999, "syntax", "header", options, "footer")
        System.exit(1)
        null
    }
  }
}
