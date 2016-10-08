package org.pico.event.datadog

import io.circe.Encoder
import io.circe.syntax._

sealed trait MetricType

case object Gauge extends MetricType

object MetricType {
  implicit val metricType_Encoder_wcKUmK9 = Encoder.instance[MetricType] {
    case Gauge => "gauge".asJson
  }
}
