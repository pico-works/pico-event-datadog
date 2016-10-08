package org.pico.event.datadog

import org.pico.event.http.client.HttpClient

trait DatadogClient {

}

object DatadogClient {
  def apply(httpClient: HttpClient): DatadogClient = ???
}
