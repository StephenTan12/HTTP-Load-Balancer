import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.Socket

class ClientToServerSocketHandler(client: Socket) {
    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ClientToServerSocketHandler::class.java)
    }

    private val clientOutput = client.getOutputStream()

    fun run(port: Int) {
        try {
            clientOutput.write("GET / HTTP/1.1\r\nHost:localhost:$port\r\n\r\n".toByteArray())
        } catch (ex: Exception) {
            logger.error("Error when writing to server: ${ex.message}")
        }
    }
}