package clientwrapper
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients

/**
 * Wrapper class for calling the Overpass API.
 * @param endpoint
 */
class OverpassWrapper(endpoint: String) {
  def getFeatures(queryText: String): String = {
    val uriBuilder = new URIBuilder(endpoint)
    uriBuilder.addParameter("data", queryText)
    // todo see if/how these http resources should be closed in the common case and in an error case
    val httpclient = HttpClients.createDefault
    val response = httpclient.execute(new HttpGet(uriBuilder.build()))
    IOUtils.toString(response.getEntity.getContent, StandardCharsets.UTF_8)
  }
}
