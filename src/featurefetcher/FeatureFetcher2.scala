package featurefetcher

import converter.FormatConverter2
import persistence.LocalFilePersister2

class FeatureFetcher2 {
  def fetchFeatures(dataSource: String,
                    featureName: String,
                    outputFormat: String,
                    overpassEndpoint: String,
                    boundaryCoordinates: String,
                    baseFolder: String,
                    osmXmlToGeoJsonToolPath: String) {

    val featureDownloader = new FeatureDownloader2(overpassEndpoint)
    val featureDataAndFormat: DataAndFormat = featureDownloader.downloadFeatureData(dataSource, featureName, outputFormat, boundaryCoordinates)
    val formatConverter = new FormatConverter2(osmXmlToGeoJsonToolPath)
    val convertedFeatureDataAndFormat = formatConverter.convert(featureDataAndFormat, outputFormat)
    val localFilePersister2 = new LocalFilePersister2
    localFilePersister2.persist(baseFolder, convertedFeatureDataAndFormat.data, featureName, convertedFeatureDataAndFormat.format)
  }
}

case class DataAndFormat(data: String, format: String)

