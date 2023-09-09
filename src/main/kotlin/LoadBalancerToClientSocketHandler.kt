import org.slf4j.Logger
import org.slf4j.LoggerFactory
import utils.BackendServers
import java.lang.Exception
import java.net.Socket

class LoadBalancerToClientSocketHandler(private val client: Socket) {
    private val clientOutput = client.getOutputStream()
    private var backendSocket: Socket? = null

    private companion object {
        const val HOSTNAME = "127.0.0.1"
        val logger: Logger = LoggerFactory.getLogger(LoadBalancerToClientSocketHandler::class.java)
    }

    suspend fun handleClient() {
        try {
            val serverPort = BackendServers.getPort()
            logger.info("Retrieved port $serverPort")

            backendSocket = Socket(HOSTNAME, serverPort)
            logger.info("Started connection to $backendSocket")

            ClientToServerSocketHandler(backendSocket!!).run(serverPort)

            val text = backendSocket!!.getInputStream().readBytes()
            clientOutput.write(text)
        } catch (ex: Exception) {
            logger.error("Error when trying to get output from server: ${ex.message}")
        } finally {
            shutdown()
        }
    }

    private fun shutdown() {
        backendSocket?.close()
        client.close()
        logger.info("Closed connection with $client and $backendSocket")
    }
}