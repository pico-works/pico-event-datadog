package org.pico.event.datadog

import org.specs2.mutable.Specification
import io.circe.syntax._

class SeriesSpec extends Specification {
  "Series should encode" in {
    val series = DatadogMetrics(
      series = List(
        Metric(
          name = "test.metric",
          points = List((System.currentTimeMillis() / 1000) -> 20L),
          metricType = Gauge,
          host = "test.example.com",
          tags = List(Tag("environment:test")))))

    println(series.asJson.spaces2)

    ok
  }
}
