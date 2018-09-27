package scalafmtlsp

import io.circe.Json
import scala.meta.jsonrpc._
import scala.meta.lsp._
import scribe._

object ScalafmtLspServices {

  private lazy val documentFormattingProvider = new DocumentFormattingProvider()

  def build(logger: LoggerSupport, client: LanguageClient): Services = {
    Services
      .empty(logger)
      .request(Lifecycle.initialize) { params =>
        logger.info(params.toString)
        val capabilities = ServerCapabilities(
          documentFormattingProvider = true
        )
        InitializeResult(capabilities)
      }
      .notification(Lifecycle.initialized) { _ =>
        // NOTE: It's possible to send a notification to the client at any point
        Window.showMessage.info("Hello world!")(client)

        // notification handlers can't respond so return Unit
        logger.info("Client is initialized")
      }
      .request(Lifecycle.shutdown) { _ =>
        logger.info("Client is about to call exit soon")
        Json.Null
      }
      .notification(Lifecycle.exit) { _ =>
        logger.info("Goodbye!")
        System.exit(0)
      }
      .requestAsync(TextDocument.formatting) { params =>
        // val uri = Uri(params.textDocument)
        documentFormattingProvider.format()
      }
  }
}
