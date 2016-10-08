package org.pico.event.datadog

import io.circe.Encoder
import io.circe.syntax._

case class Metric(
    name: String,
    points: List[(Long, Long)],
    metricType: MetricType,
    host: String,
    tags: List[Tag])

object Metric {
  implicit val metric_Encoder_Jza2hrp = Encoder.instance[Metric] { metric =>
    Map(
      "metric"      -> metric.name.asJson,
      "points"      -> metric.points.asJson,
      "type"        -> metric.metricType.asJson,
      "host"        -> metric.host.asJson,
      "tags"        -> metric.tags.asJson
    ).asJson
  }
}
