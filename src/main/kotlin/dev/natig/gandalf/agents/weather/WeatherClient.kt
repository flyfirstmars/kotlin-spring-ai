package dev.natig.gandalf.agents.weather

import java.net.URI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class WeatherClient(
    private val webClient: WebClient = WebClient.builder()
        .baseUrl("https://api.open-meteo.com")
        .build(),
    private val ioDispatcher: CoroutineDispatcher
) {
    private val logger = LoggerFactory.getLogger(WeatherClient::class.java)

    private val locationCoordinates = mapOf(
        "amsterdam" to Pair(52.3676, 4.9041),
        "andorra la vella" to Pair(42.5063, 1.5218),
        "athens" to Pair(37.9838, 23.7275),
        "belgrade" to Pair(44.7866, 20.4489),
        "berlin" to Pair(52.5200, 13.4050),
        "bern" to Pair(46.9480, 7.4474),
        "bratislava" to Pair(48.1486, 17.1077),
        "brussels" to Pair(50.8503, 4.3517),
        "bucharest" to Pair(44.4268, 26.1025),
        "budapest" to Pair(47.4979, 19.0402),
        "chisinau" to Pair(47.0105, 28.8638),
        "copenhagen" to Pair(55.6761, 12.5683),
        "dublin" to Pair(53.3498, -6.2603),
        "helsinki" to Pair(60.1699, 24.9384),
        "kiev" to Pair(50.4501, 30.5234),
        "lisbon" to Pair(38.7223, -9.1393),
        "ljubljana" to Pair(46.0569, 14.5058),
        "london" to Pair(51.5074, -0.1278),
        "luxembourg city" to Pair(49.6116, 6.1319),
        "madrid" to Pair(40.4168, -3.7038),
        "minsk" to Pair(53.9006, 27.5590),
        "monaco" to Pair(43.7384, 7.4246),
        "moscow" to Pair(55.7558, 37.6176),
        "oslo" to Pair(59.9139, 10.7522),
        "paris" to Pair(48.8566, 2.3522),
        "podgorica" to Pair(42.4304, 19.2594),
        "prague" to Pair(50.0755, 14.4378),
        "riga" to Pair(56.9496, 24.1052),
        "rome" to Pair(41.9028, 12.4964),
        "sarajevo" to Pair(43.8563, 18.4131),
        "skopje" to Pair(41.9981, 21.4254),
        "sofia" to Pair(42.6977, 23.3219),
        "stockholm" to Pair(59.3293, 18.0686),
        "tallinn" to Pair(59.4370, 24.7536),
        "tirana" to Pair(41.3275, 19.8187),
        "vaduz" to Pair(47.1410, 9.5209),
        "valletta" to Pair(35.8989, 14.5146),
        "vienna" to Pair(48.2082, 16.3738),
        "vilnius" to Pair(54.6872, 25.2797),
        "warsaw" to Pair(52.2297, 21.0122),
        "tbilisi" to Pair(41.7151, 44.8271),
        "ankara" to Pair(39.9208, 32.8541),
        "baku" to Pair(40.4093, 49.8671),
        "beirut" to Pair(33.8938, 35.5018),
        "amman" to Pair(31.9454, 35.9284),
        "jerusalem" to Pair(31.7683, 35.2137),
        "damascus" to Pair(33.5138, 36.2765),
        "riyadh" to Pair(24.7136, 46.6753),
        "abu dhabi" to Pair(24.4539, 54.3773),
        "muscat" to Pair(23.5859, 58.4059),
        "kuwait city" to Pair(29.3759, 47.9774),
        "manama" to Pair(26.2285, 50.5860),
        "doha" to Pair(25.2854, 51.5310)
    )

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchWeather(city: String): WeatherResponse? {
        val coords = locationCoordinates[city.lowercase()] ?: return null
        val (latitude, longitude) = coords

        return try {
            val uri: URI = UriComponentsBuilder.fromUriString("https://api.open-meteo.com")
                .path("/v1/forecast")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current_weather", true)
                .queryParam("timezone", "auto")
                .build()
                .toUri()

            val responseString: String? = withContext(ioDispatcher) {
                webClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus({ it.isError }) { clientResponse ->
                        logger.error("Error fetching weather data for $city: ${clientResponse.statusCode()}")
                        Mono.error(Exception("Failed to fetch weather data"))
                    }
                    .bodyToFlux(String::class.java)
                    .asFlow()
                    .firstOrNull()
            }
            responseString?.let { json.decodeFromString(WeatherResponse.serializer(), it) }
        } catch (ex: Exception) {
            logger.error("Exception while fetching weather for $city: ${ex.message}")
            null
        }
    }
}