package org.pico.event.datadog

import com.typesafe.config.ConfigFactory
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
  val apiKey = config.getString("datadog.api.key")
  val appKey = config.getString("datadog.app.key")

  def nowSeconds: Long = System.currentTimeMillis() / 1000

  val httpGetMetrics = HttpPost(
    url = s"https://app.datadoghq.com/api/v1/series?api_key=$apiKey",
    entity = ApplicationJsonEntity(
      s"""
        |{ "series" :
        |  [ { "metric":"test.metric"
        |    , "points": [[$nowSeconds, 20]]
        |    , "type": "gauge"
        |    , "host": "test.example.com"
        |    , "tags": ["environment:test"]
        |    }
        |  ]
        |}
      """.stripMargin))

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
