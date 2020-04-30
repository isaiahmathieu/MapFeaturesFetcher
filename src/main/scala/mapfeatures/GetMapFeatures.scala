package mapfeatures

import featurefetcher.FeatureFetcher
import org.apache.commons.cli.{CommandLine, DefaultParser, HelpFormatter, Option, Options}

import scala.io.Source

object GetMapFeatures {

  val BoundaryCoordinatesFile = "boundary_coordinates_file"
  val OutputFolder = "output_folder"
  val Features = "features"
  val OutputFormat = "output_format"
  val OverpassEndpoint = "overpass_endpoint"
  val NodeJsPath = "nodejs_path"
  val OsmtogeojsonPath = "osmtogeojson_path"
  val DataSource = "data_source"
  val Help = "help"
  val options = new Options
  options.addOption(new Option(BoundaryCoordinatesFile, BoundaryCoordinatesFile, true, "path to file containing coordinates of boundary"))
  options.addOption(new Option(OutputFolder, OutputFolder, true, "path to folder into which results should be written"))
  options.addOption(new Option(Features, Features, true, s"list of one or more specific features to download. each feature type should be separated by a space. possible values are: ${constant.Features.values.map(x => x.toString).mkString(",")}"))
  options.addOption(new Option(OutputFormat, OutputFormat, true, s"output format. possible values are: ${constant.OutputFormats.values.map(x => x.toString).mkString(",")}"))
  options.addOption(new Option(OverpassEndpoint, OverpassEndpoint, true, "endpoint for the Overpass API"))
  options.addOption(new Option(NodeJsPath, NodeJsPath, true, "Path to nodejs executable. only required if conversion to geojson is needed. If omitted and conversion to geojson is requested, this path must be set by a jvm property called 'nodeJsPath'"))
  options.addOption(new Option(OsmtogeojsonPath, OsmtogeojsonPath, true, "path to nodejs osmtogeojson executable that converts osm xml to geojson. only required if conversion to geojson is needed. If omitted and conversion to geojson is requested, this path must be set by a jvm property called 'osmtogeojsonPath'"))
  options.addOption(new Option(DataSource, DataSource, true, "data source, currently the only valid argument for this is 'osm' (Open Street Maps)"))
  options.addOption(new Option(Help, Help, false, "print usage"))

  def main(args: Array[String]): Unit = {
    val commandLine = parseArgs(args)
    if (commandLine.hasOption(Help)) {
      // print usage and exit
      printHelp()
      System.exit(0)
    }
    // todo validate args
    val boundaryCoordinates = parseBoundaryCoordinates(commandLine.getOptionValue(BoundaryCoordinatesFile))
    val outputFolder = commandLine.getOptionValue(OutputFolder)
    val features = commandLine.getOptionValue(Features)
    val outputFormat = commandLine.getOptionValue(OutputFormat)
    val overpassEndpoint = commandLine.getOptionValue(OverpassEndpoint)
    val nodeJsPath = scala.Option(commandLine.getOptionValue(NodeJsPath)).getOrElse(System.getProperty("nodeJsPath"))
    val osmToGeoJsonPath = scala.Option(commandLine.getOptionValue(OsmtogeojsonPath)).getOrElse(System.getProperty("osmtogeojsonPath"))
    val dataSource = commandLine.getOptionValue(DataSource)
    val featureNames = features.split("\\s+")
    val featureFetcher = new FeatureFetcher
    // for each feature, download and persist the data
    featureNames.foreach(
      feature => featureFetcher.fetchFeatures(dataSource, feature, outputFormat,
                overpassEndpoint, boundaryCoordinates, outputFolder, nodeJsPath, osmToGeoJsonPath))
  }

  /**
   * parses the boundary coordinates file, which must be a text file containing one line of the format:
   * latitude_1 longitude_1 latitude_2 longitude_2 latitude_3 longitude_3
   * where each pair of latitude and longitude are coordinates of the boundary polygon for the query
   * @param boundaryCoordinatesFile
   * @return the file contents as a String
   */
  private def parseBoundaryCoordinates(boundaryCoordinatesFile: String): String = {
    val source = Source.fromFile(boundaryCoordinatesFile)
    val coordinates = source.mkString.trim
    source.close()
    coordinates
  }

  /**
   * parses the commandline arguments
   * @param args
   * @return CommandLine object containing the input arguments.
   */
  def parseArgs(args: Array[String]): CommandLine = {
    try {
      (new DefaultParser).parse(options, args)
    } catch {
      case e: Exception =>
        println(e.getMessage)
        System.exit(1)
        null
    }
  }

  def printHelp(): Unit = {
    (new HelpFormatter).printHelp(99999, "syntax", "header", options, "footer")
  }
}
