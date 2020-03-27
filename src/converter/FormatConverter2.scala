package converter

import java.io.File

import featurefetcher.DataAndFormat

class FormatConverter2(osmToGeoJsonToolPath: String) {
  // todo refactor this logic so that it can be used in the validation step
  def convert(dataAndFormat: DataAndFormat, outputFormat: String): DataAndFormat = {
    if (dataAndFormat.format.equals(outputFormat)) {
      dataAndFormat
    } else if ("xml".equals(dataAndFormat.format) && "geojson".equals(outputFormat)) {
      // convert xml to geojson
      // save the data to a temporary file because the tool that does the conversion reads input from a file
      val xmlTempFile = File.createTempFile("temp_osm_xml_",".xml")
      xmlTempFile.deleteOnExit()
      val xmlTempFilePath = xmlTempFile.getAbsoluteFile
      import java.io._
      val pw = new PrintWriter(xmlTempFile)
      pw.write(dataAndFormat.data)
      pw.close()
      import scala.sys.process._
      // !! operator executes the external command and captures the output
      val convertedDate = s"""node "$osmToGeoJsonToolPath" "$xmlTempFilePath" """.!!
      DataAndFormat(data = convertedDate, format = "geojson")
    } else  {
      // eventually I would put other conversion logic here. there will be a branch for every conversion possible
      throw new NotImplementedError(s"can't convert from ${dataAndFormat.format} to $outputFormat")
    }
  }
}
