package clientwrapper
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.net.URIBuilder

trait MapFeatureClientWrapper {
  def getFeatures(queryText: String): String
}

class OverpassWrapper(endpoint: String) extends MapFeatureClientWrapper {
  override def getFeatures(queryText: String): String = {
    val httpclient = HttpClients.createDefault
    val uriBuilder = new URIBuilder(endpoint)
    uriBuilder.addParameter("data", queryText)
    val response = httpclient.execute(new HttpGet(uriBuilder.build()))
    IOUtils.toString(response.getEntity.getContent, StandardCharsets.UTF_8)
  }
}

