package org.pico.event.datadog

import io.circe.Encoder
import io.circe.syntax._

case class Tag(name: String) extends AnyVal

object Tag {
  implicit val encoderTag_xKufnYX = Encoder.instance[Tag](_.name.asJson)
}
