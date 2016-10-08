package org.pico.event.datadog

import com.typesafe.config.ConfigFactory
import io.circe.syntax._
import org.pico.disposal.std.autoCloseable._
import org.pico.disposal.{Auto, Eval}
import org.pico.event.http.client._
import org.pico.event.http.client.model._
import org.pico.event.syntax.source._
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

class PostMetricsSpec extends Specification {
  val config = ConfigFactory.load()
  val apiKey = DatadogApiKey(config.getString("datadog.api.key"))
  val appKey = DatadogAppKey(config.getString("datadog.app.key"))

  def nowSeconds: Long = System.currentTimeMillis() / 1000

  val series = Series(
    series = List(
      Metric(
        name = "test.metric",
        points = List(nowSeconds -> 50L),
        metricType = Gauge,
        host = "test.example.com",
        tags = List(Tag("environment:test")))))

  println(series.asJson.spaces2)

  val httpGetMetrics = HttpPost(
    url = s"https://app.datadoghq.com/api/v1/series?api_key=${apiKey.value}",
    entity = ApplicationJsonEntity(series.asJson.noSpaces))

  println(httpGetMetrics)

  "Post metrics" in {
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
      Thread.sleep(7000)
    }

    ok
  }
}
