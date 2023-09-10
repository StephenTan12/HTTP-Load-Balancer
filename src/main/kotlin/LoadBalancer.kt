import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import utils.BackendServers
import java.net.ServerSocket

class LoadBalancer {
    private companion object {
        const val PORT = 80
        val logger: Logger = LoggerFactory.getLogger(LoadBalancer::class.java)
    }

    suspend fun start() {
        val server = ServerSocket(PORT)
        logger.info("Listening on port ${server.localPort}")

        coroutineScope {
            launch(Dispatchers.IO) {
                BackendServers.checkHealthServers()
            }

            while (true) {
                val client = server.accept()
                logger.info("Accepted client connection from: $client")

                launch(Dispatchers.IO) {
                    ClientRequestHandler.handleClient(client)
                }
            }
        }
    }
}