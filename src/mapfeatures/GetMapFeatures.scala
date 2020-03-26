package mapfeatures

import clientwrapper.OverpassWrapper
import converter.FormatConverter
import featurefetcher.{FeatureFetcher, FeatureFetcherFactory}
import org.apache.commons.cli.{CommandLine, CommandLineParser, DefaultParser, Option, Options, ParseException}
import persistence.LocalFilePersister

import scala.io.Source

object GetMapFeatures {

  val BoundaryCoordinatesFile = "boundary_coordinates_file"
  val OutputFolder = "output_folder"
  val Features = "features"
  val OutputFormat = "output_format"
  val OverpassEndpoint = "overpass_endpoint"

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

    val wrapper = new OverpassWrapper(overpassEndpoint)

    val featureNames = features.split("\\s+")
    case class FeatureNameAndFetcher(name: String, fetcher: FeatureFetcher)
    val featureNamesAndFetchers = featureNames.map(x =>
      FeatureNameAndFetcher(x, FeatureFetcherFactory.apply(x, boundaryCoordinates, outputFormat, wrapper, null)))
    case class FeatureNameAndData(name: String, data: String)
    val featureNamesAndXmlData = featureNamesAndFetchers.map(featureFetcher => FeatureNameAndData(featureFetcher.name, featureFetcher.fetcher.fetchFeatures()))
    val formatConverter = new FormatConverter
    val featureNamesAndGeojsonData = featureNamesAndXmlData.map(x => FeatureNameAndData(x.name, formatConverter.convertOsmXmlToGeoJson(x.data)))
    val featurePersister = new LocalFilePersister(outputFolder, outputFormat)
    featureNamesAndGeojsonData.foreach(x => featurePersister.persist(x.name, x.data ))
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
