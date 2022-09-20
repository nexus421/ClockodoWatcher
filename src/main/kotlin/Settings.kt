import kotlinx.serialization.Serializable

@Serializable
data class Settings(val userEmail: String, val apiKey: String, val reuqestLogType: String) //NONE, INFO, ALL, BODY, HEADER

