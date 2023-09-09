import kotlinx.coroutines.runBlocking

suspend fun main() {
    val lb = LoadBalancer()
    runBlocking {
        lb.start()
    }
}