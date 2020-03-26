package clientwrapper
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.core5.net.URIBuilder

trait MapFeatureClientWrapper {
  def getFeatures(queryText: String): String
}

class OverpassWrapper(endpoint: String) extends MapFeatureClientWrapper {

  override def getFeatures(queryText: String): String = {
    val httpclient = HttpClients.createDefault
    val uriBuilder = new URIBuilder(endpoint)
    uriBuilder.addParameter("data", queryText)
    val httpGet = new HttpGet(uriBuilder.build())
    val response1 = httpclient.execute(httpGet)
    val entity1 = response1.getEntity
    val is = entity1.getContent
    IOUtils.toString(is, StandardCharsets.UTF_8)
  }
}

object XXX {
  def main(args: Array[String]): Unit = {
    val wrapper = new OverpassWrapper("https://overpass.kumi.systems/api/interpreter")
    wrapper.getFeatures("oijjioojioij")
  }
}