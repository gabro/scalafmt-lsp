package scalafmtlsp

import monix.execution.Scheduler
import java.util.concurrent.Executors
import scala.meta.jsonrpc.InputOutput
import scala.meta.jsonrpc.Connection
import scribe.Logger
import scribe.writer.FileWriter
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.nio.file.Paths

object Main {
  def main(args: Array[String]): Unit = {
    val scheduler = Scheduler(Executors.newFixedThreadPool(4))
    val io = new InputOutput(System.in, System.out)
    val logger =
      Logger("scalafmt-lsp").withHandler(writer = FileWriter.simple()

    val connection =
      Connection(io, serverLogger = logger, clientLogger = logger) { client =>
        ScalafmtLspServices.build(logger, client)
      }(scheduler)

    Await.result(connection.server, Duration.Inf)
  }
}
