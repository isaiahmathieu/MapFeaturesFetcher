package clientwrapper
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients

class OverpassWrapper(endpoint: String) {
  def getFeatures(queryText: String): String = {
    val httpclient = HttpClients.createDefault
    val uriBuilder = new URIBuilder(endpoint)
    uriBuilder.addParameter("data", queryText)
    val response = httpclient.execute(new HttpGet(uriBuilder.build()))
    IOUtils.toString(response.getEntity.getContent, StandardCharsets.UTF_8)
  }
}

