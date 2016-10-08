package org.pico.event.datadog

import com.typesafe.config.ConfigFactory
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.{Auto, Eval}
import org.pico.event.http.client._
import org.pico.event.http.client.model.{HttpGet, HttpOk}
import org.pico.event.syntax.source._
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

class PostMetricsSpec extends Specification {
  val config = ConfigFactory.load()
  val apiKey = config.getString("datadog.api.key")
  val appKey = config.getString("datadog.app.key")

  def nowSeconds: Long = System.currentTimeMillis() / 1000

  val httpGetMetrics = HttpGet(s"https://app.datadoghq.com/api/v1/metrics?api_key=$apiKey&application_key=$appKey&from=$nowSeconds")

  "Download metrics" in {
    for {
      httpClient      <- Auto(HttpClient())
      _               <- Eval(httpClient.impl.start())
      httpSinkSource  <- Auto(httpClient.sinkSource)
      httpSource      <- Auto(httpSinkSource.scheduled)
      _               <- Auto(httpSource.subscribe { v =>
        val inContent = v.right.get.asInstanceOf[HttpOk].impl.getEntity.getContent
        val content: String = Source.fromInputStream(inContent).mkString

        println(s"Content: $content")
      })
      _               <- Eval(httpSinkSource.publish(httpGetMetrics))
    } {
      Thread.sleep(2000)
    }

    ok
  }
}
