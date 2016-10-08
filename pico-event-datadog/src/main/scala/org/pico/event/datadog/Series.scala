package org.pico.event.datadog

import io.circe.Encoder
import io.circe.syntax._

case class Series(
    series: List[Metric])

object Series {
  implicit val encoder_Series_YUrKb99 = Encoder.instance[Series] { series =>
    Map("series" -> series.series.asJson).asJson
  }
}
