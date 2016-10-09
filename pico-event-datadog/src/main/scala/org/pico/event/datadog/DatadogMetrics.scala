package org.pico.event.datadog

import io.circe.Encoder
import io.circe.syntax._

case class DatadogMetrics(series: List[Metric])

object DatadogMetrics {
  implicit val encoder_Series_YUrKb99 = Encoder.instance[DatadogMetrics] { series =>
    Map("series" -> series.series.asJson).asJson
  }
}
