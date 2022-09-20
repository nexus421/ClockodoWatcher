import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.time.LocalDate

object Requests {

    private fun getClient() = HttpClient(Java) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = false
                ignoreUnknownKeys = true
            })
        }
        engine {
            pipelining = true
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("Logger Ktor => $message")
                }
            }
            level = LogLevel.values().find { it.name == SettingsHandler.settings.reuqestLogType } ?: LogLevel.NONE
        }

        install(ResponseObserver) {
            onResponse { response ->
                println("HTTP status: ${response.status.value}")
            }
        }
    }


    suspend fun getUserStats(): UserStatsSmall? = getClient().use { client ->
        try {
            val response = client.get("https://my.clockodo.com/api/userreports?year=${LocalDate.now().year}&type=4") {
                header("X-ClockodoApiUser", SettingsHandler.settings.userEmail)
                header("X-ClockodoApiKey", SettingsHandler.settings.apiKey)
            }.body<UserStats>()

            val result = response.userreports.map { userResport ->
                val dayStats = userResport.month_details.flatMap { monthDetail ->
                    monthDetail.week_details.flatMap { weekDetail ->
                        weekDetail.day_details.mapNotNull { dayDetail ->
                            if (dayDetail.hours == null || dayDetail.work_start == null || dayDetail.work_end == null) return@mapNotNull null
                            DayStats(
                                dayDetail.date,
                                dayDetail.work_start,
                                dayDetail.work_end,
                                Time(dayDetail.hours / 3600, (dayDetail.hours % 3600) / 60),
                                dayDetail.getBreakTime()
                            )
                        }
                    }
                }
                UserStatsSmall(userResport.users_name, userResport.users_id, userResport.users_email, dayStats)
            }

            result.first()
        } catch (e: Exception) {
            System.err.println(e)
            null
        }
    }

    private fun Int.toHoursString(): String {
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        return "${hours}h ${minutes}m"
    }

    private fun breaksToString(breaks: List<Break>) =
        breaks.joinToString { "${it.since} - ${it.until} (${it.length.toHoursString()})" }


}