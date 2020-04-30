package featurefetcher

import converter.FormatConverter
import persistence.LocalFilePersister

class FeatureFetcher {
  /**
   * Downloads the specified feature from the specified data source (such as OpenStreetMaps) and saves it at the
   * specified location in the specified output format
   * @param dataSource
   * @param featureName
   * @param outputFormat
   * @param overpassEndpoint
   * @param boundaryCoordinates
   * @param baseFolder
   * @param nodeJsPath
   * @param osmToGeoJsonPath
   */
  def fetchFeatures(dataSource: String,
                    featureName: String,
                    outputFormat: String,
                    overpassEndpoint: String,
                    boundaryCoordinates: String,
                    baseFolder: String,
                    nodeJsPath: String,
                    osmToGeoJsonPath: String) {
    // todo have FeatureDownloader be passed in so that multiple FeatureFetchers use the same feature downloader
    val featureDownloader = new FeatureDownloader(overpassEndpoint)
    val featureDataAndFormat = featureDownloader.downloadFeatureData(dataSource, featureName, outputFormat, boundaryCoordinates)
    val formatConverter = new FormatConverter(nodeJsPath, osmToGeoJsonPath)
    val convertedFeatureDataAndFormat = formatConverter.convert(featureDataAndFormat, outputFormat)
    // todo have a FilePersister be an input parameter so that unit tests can pass in one that doesn't write to disk
    val filePersister = new LocalFilePersister(baseFolder)
    filePersister.persist(convertedFeatureDataAndFormat.data, featureName, convertedFeatureDataAndFormat.format, dataSource)
  }
}

case class DataAndFormat(data: String, format: String)
