package org.pico.event.datadog

import com.typesafe.config.ConfigFactory
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.{Auto, Eval}
import org.pico.event.http.client._
import org.pico.event.http.client.model.HttpGet
import org.pico.event.syntax.source._
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext.Implicits.global

class MetricsSpec extends Specification {
  val config = ConfigFactory.load()
  val apiKey = config.getString("datadog.api.key")
  val appKey = config.getString("datadog.app.key")

  "Download metrics" in {
    for {
      httpClient      <- Auto(HttpClient())
      _               <- Eval(httpClient.impl.start())
      httpSinkSource  <- Auto(httpClient.sinkSource)
      httpSource      <- Auto(httpSinkSource.scheduled)
      _               <- Auto(httpSource.subscribe(v => println(s"Result: $v")))
      _               <- Eval(httpSinkSource.publish(HttpGet("http://google.com")))
    } {
      Thread.sleep(1000)
    }

    ok
  }
}
