package featurefetcher

import converter.FormatConverter
import persistence.LocalFilePersister

class FeatureFetcher {
  def fetchFeatures(dataSource: String,
                    featureName: String,
                    outputFormat: String,
                    overpassEndpoint: String,
                    boundaryCoordinates: String,
                    baseFolder: String,
                    osmXmlToGeoJsonToolPath: String) {

    val featureDownloader = new FeatureDownloader(overpassEndpoint)
    val featureDataAndFormat = featureDownloader.downloadFeatureData(dataSource, featureName, outputFormat, boundaryCoordinates)
    val formatConverter = new FormatConverter(osmXmlToGeoJsonToolPath)
    val convertedFeatureDataAndFormat = formatConverter.convert(featureDataAndFormat, outputFormat)
    val filePersister = new LocalFilePersister(baseFolder)
    filePersister.persist(convertedFeatureDataAndFormat.data, featureName, convertedFeatureDataAndFormat.format, dataSource)
  }
}

case class DataAndFormat(data: String, format: String)
