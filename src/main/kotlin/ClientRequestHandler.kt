import org.slf4j.Logger
import org.slf4j.LoggerFactory
import utils.BackendServers
import java.lang.Exception
import java.net.Socket

object ClientRequestHandler {
    private const val HOSTNAME = "127.0.0.1"
    private val logger: Logger = LoggerFactory.getLogger(ClientRequestHandler::class.java)

     suspend fun handleClient(client: Socket) {
        var backendSocket: Socket? = null

        try {
            val clientOutput = client.getOutputStream()

            val serverPort = BackendServers.getPort()
            logger.info("Retrieved port $serverPort")

            backendSocket = Socket(HOSTNAME, serverPort)
            logger.info("Started connection to $backendSocket")

            writeToBackendServer(backendSocket, serverPort)

            val text = backendSocket.getInputStream().readBytes()
            clientOutput.write(text)
        } catch (ex: Exception) {
            logger.error("Error when trying to get output from server: ${ex.message}")
        } finally {
            shutdown(backendSocket, client)
        }
    }

    fun writeToBackendServer(backendSocket: Socket, port: Int) {
        val clientOutput = backendSocket.getOutputStream()

        try {
            clientOutput.write("GET / HTTP/1.1\r\nHost:localhost:$port\r\n\r\n".toByteArray())
        } catch (ex: Exception) {
            logger.error("Error when writing to server: ${ex.message}")
        }
    }

    private fun shutdown(backendSocket: Socket?, client: Socket) {
        backendSocket?.close()
        client.close()
        logger.info("Closed connection with $client and $backendSocket")
    }
}