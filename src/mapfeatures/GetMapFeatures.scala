package mapfeatures

import clientwrapper.OverpassWrapper
import featurefetcher.FeatureFetcherFactory
import org.apache.commons.cli.{CommandLine, CommandLineParser, DefaultParser, Option, Options, ParseException}

import scala.io.Source

object GetMapFeatures {

  val BoundaryCoordinatesFile = "boundary_coordinates_file"
  val OutputFolder = "output_folder"
  val Features = "features"
  val OutputFormat = "output_format"
  val OverpassEndpoint = "overpass_endpoint"

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

    val wrapper = new OverpassWrapper(overpassEndpoint)
    val riverFetcher = FeatureFetcherFactory.apply("rivers", boundaryCoordinates, outputFormat, wrapper, null)
    val riverData = riverFetcher.fetchFeatures()
    println(riverData)
  }
  private def parseBoundaryCoordinates(boundaryCoordinatesFile: String): String = {
    Source.fromFile(boundaryCoordinatesFile).getLines.mkString.trim
  }

  def parseArgs(args: Array[String]): CommandLine = {
    val options = new Options
    options.addOption(new Option(BoundaryCoordinatesFile, BoundaryCoordinatesFile, true, "path to file containing coordinates of boundary"))
    options.addOption(new Option(OutputFolder, OutputFolder, true, "path to directory into which results should be written"))
    options.addOption(new Option(Features, Features, true, "list of one or more specific features to download. If empty or omitted, all features are downloaded"))
    options.addOption(new Option(OutputFormat, OutputFormat, true, "output format"))
    options.addOption(new Option(OverpassEndpoint, OverpassEndpoint, true, "endpoint for the overpass API"))
    val parser = new DefaultParser

    try parser.parse(options, args)
    catch {
      case e: ParseException =>
        throw new IllegalArgumentException(e.getMessage)
    }
  }

}
