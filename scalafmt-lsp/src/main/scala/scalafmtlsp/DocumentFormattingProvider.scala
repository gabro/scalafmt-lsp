package scalafmtlsp

// import scala.meta.Input
import scala.meta.lsp.TextEdit
import scala.meta.jsonrpc.Response
import monix.eval.Task

class DocumentFormattingProvider() {
  def format(
      // input: Input.VirtualFile
  ): Task[Either[Response.Error, List[TextEdit]]] = Response.okAsync(Nil)
}
