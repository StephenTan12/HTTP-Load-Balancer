package utils

import ClientRequestHandler.writeToBackendServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.Socket

object BackendServers {
    private val ports: List<Int> = arrayListOf(
        8080,
        8081
    )
    private const val HOSTNAME = "127.0.0.1"
    private val logger: Logger = LoggerFactory.getLogger(BackendServers::class.java)
    private val availablePorts: ArrayList<Int> = arrayListOf()
    private var usedPortCount = 0

    private val mutex = Mutex()

    suspend fun checkHealthServers() {
        while(true) {
            mutex.lock()
            logger.info("Checking Server Health")
            availablePorts.clear()

            for (port in ports) {
                logger.info("Checking Server Health of Port $port")
                var socket: Socket? = null

                try {
                    socket = Socket(HOSTNAME, port)

                    writeToBackendServer(socket, port)

                    val byteArray =
                        socket.getInputStream().readBytes()
                    if (byteArray.decodeToString()
                            .split(" ")[1].contains("200")
                    ) {
                        availablePorts.add(port)
                        logger.info("Server $port is healthy")
                    } else {
                        logger.info("Server $port is unhealthy")
                    }
                } catch (ex: Exception) {
                    logger.error("Error when getting output from server $port: ${ex.message}")
                } finally {
                    usedPortCount = 0
                    socket?.close()
                }
            }
            mutex.unlock()
            delay(10000)
        }
    }

    suspend fun getPort(): Int {
        mutex.lock()
        val port = availablePorts[usedPortCount%availablePorts.size]
        mutex.unlock()
        usedPortCount++
        return port
    }
}