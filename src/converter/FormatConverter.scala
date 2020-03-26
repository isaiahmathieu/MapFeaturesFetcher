package converter

import java.io.File

trait FormatConverter {
  def convert(dataToConvert: String): String
}

class OsmXmlToGeoJsonConverter(private val osmToGeoJsonToolPath: String) extends FormatConverter {

  override def convert(dataToConvert: String): String = {
    // save the data to a temporary file because the tool that does the conversion reads input from a file
    val xmlTempFile = File.createTempFile("temp_osm_xml_",".xml")
    xmlTempFile.deleteOnExit()
    val xmlTempFilePath = xmlTempFile.getAbsoluteFile
    import java.io._
    val pw = new PrintWriter(xmlTempFile)
    pw.write(dataToConvert)
    pw.close()
    import scala.sys.process._
    // !! operator executes the external command and captures the output
    s"""node "$osmToGeoJsonToolPath" "$xmlTempFilePath" """.!!
  }
}
