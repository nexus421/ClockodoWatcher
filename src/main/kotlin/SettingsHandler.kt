import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object SettingsHandler {

    private val json = Json {
        prettyPrint = true
    }
    lateinit var settings: Settings
    private val settingsFile = File(FileHandler.workingDir + FileHandler.configFile).apply {
        if(exists().not() || isFile.not()) {
            createNewFile()
            writeText(json.encodeToString(Settings("test@test.de", "1234567890", "NONE")))
        }
    }

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val settingsJson = settingsFile.readText()
        try {
            settings = Json.decodeFromString(settingsJson)
            if(settings.apiKey == "1234567890" || settings.userEmail == "test@test.de") println("Enter your email and API-KEY in the config file (${settingsFile.path})")
        } catch (e: Exception) {
            System.err.println("Error loading settings! -> ${e.message}")
        }
    }

}