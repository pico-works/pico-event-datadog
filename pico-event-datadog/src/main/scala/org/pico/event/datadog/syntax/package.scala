package org.pico.event.datadog

import io.circe.syntax._
import org.pico.event.SinkSource
import org.pico.event.http.client.model._
import cats.syntax.profunctor._

import scala.concurrent.{ExecutionContext, Future}

package object syntax {
  implicit class HttpSinkSourceOps_NGt4f6x(val self: SinkSource[HttpRequest, Future[HttpResponse]]) extends AnyVal {
    def metrics(implicit apiKey: DatadogApiKey, ec: ExecutionContext): SinkSource[DatadogMetrics, Future[DatadogResponse]] = {
      self.dimap[DatadogMetrics, Future[DatadogResponse]] { metrics =>
        HttpPost(
          url = s"https://app.datadoghq.com/api/v1/series?api_key=${apiKey.value}",
          entity = ApplicationJsonEntity(metrics.asJson.noSpaces))
      } { future =>
        future.map {
          case HttpOk(impl) =>
            println(s"Entity: ${impl.getEntity}")
            DatadogOk
        }
      }
    }
  }
}
