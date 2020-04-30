package converter

import java.io.File

import constant.OutputFormats
import featurefetcher.DataAndFormat

class FormatConverter(nodeJsPath: String, osmtogeojsonPath: String) {

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
      val convertCommand = s"$nodeJsPath $osmtogeojsonPath $xmlTempFilePath"
      // todo add DEBUG log statements that print the location of node, of osmtogeojson, and of the command that is run
      println(convertCommand)
      // !! operator executes the external command and captures the output
      import scala.sys.process._
      val convertedData = convertCommand.!!
      DataAndFormat(data = convertedData, format = OutputFormats.geojson.toString)
    } else  {
      // other conversion logic goes here in the future. there would be a branch for every conversion possible
      throw new NotImplementedError(s"can't convert from ${dataAndFormat.format} to $outputFormat")
    }
  }
}
