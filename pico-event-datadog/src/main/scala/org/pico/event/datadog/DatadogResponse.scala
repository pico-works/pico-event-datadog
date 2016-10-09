package org.pico.event.datadog

sealed trait DatadogResponse

case object DatadogOk extends DatadogResponse
