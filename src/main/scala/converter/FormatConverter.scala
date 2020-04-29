package converter

import java.io.File
import java.nio.file.FileSystems

import constant.OutputFormats
import featurefetcher.DataAndFormat

class FormatConverter(osmXmlToGeoJsonToolPath: String) {
  def convert(dataAndFormat: DataAndFormat, outputFormat: String): DataAndFormat = {
    if (outputFormat == dataAndFormat.format) {
      // no conversion needed
      dataAndFormat
    } else if (dataAndFormat.format == OutputFormats.xml.toString && outputFormat == OutputFormats.geojson.toString) {
      // convert xml to geojson
      // save the data to a temporary file because the tool that does the conversion needs to read input from a file
      val xmlTempFile = File.createTempFile("temp_osm_xml_",".xml")
      xmlTempFile.deleteOnExit()
      val xmlTempFilePath = xmlTempFile.getAbsoluteFile
      import java.io._
      val pw = new PrintWriter(xmlTempFile)
      pw.write(dataAndFormat.data)
      pw.close()
      import scala.sys.process._
      // !! operator executes the external command and captures the output
      // todo fix this so it's less hardcoded...
      val pathToNode = FileSystems.getDefault.getPath(".frontend/node/node")
      val pathToOsmGeoJson = FileSystems.getDefault.getPath("node_modules/osmtogeojson/osmtogeojson")
      val convertedData = s"${pathToNode.toAbsolutePath} ${pathToOsmGeoJson.toAbsolutePath} $xmlTempFilePath".!!
      DataAndFormat(data = convertedData, format = OutputFormats.geojson.toString)
    } else  {
      // other conversion logic goes here in the future. there would be a branch for every conversion possible
      throw new NotImplementedError(s"can't convert from ${dataAndFormat.format} to $outputFormat")
    }
  }
}
