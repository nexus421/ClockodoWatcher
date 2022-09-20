import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileHandler {

    val sdf = SimpleDateFormat("ddMMyyyy", Locale.GERMANY)
    val workingDir = "${System.getProperty("user.home")}${File.separator}Clockodo${File.separator}"
    val timeFilesFolder = "${workingDir}times${File.separator}"
    val changedEntriesFolder = "${workingDir}changed${File.separator}"
    val configFile = "config.json"

    private val json = Json {
        prettyPrint = true
    }

    init {
        File(timeFilesFolder).apply {
            if (exists().not() || isDirectory.not()) mkdirs()
        }

        File(workingDir + configFile).apply {
            if(exists().not() || isFile.not()) {
                createNewFile()
                writeText(json.encodeToString(Settings("test@test.de", "1234567890", "NONE")))
            }
        }

        File(changedEntriesFolder).apply {
            if (exists().not() || isDirectory.not()) mkdirs()
        }
    }

    fun createAndWriteNewDay(
        userStatsSmall: UserStatsSmall,
        storeType: StoreType = StoreType.JSON,
        overrideIfExists: Boolean = false
    ) {
        val filename = "time_${sdf.format(Date())}"
        val fileJson = File("$timeFilesFolder$filename.json")
        val fileText = File("$timeFilesFolder$filename.txt")
        if (storeType == StoreType.JSON || storeType == StoreType.BOTH) {
            if (overrideIfExists.not() && fileJson.exists()) {
                println("\tJSON-File exists. Ignoring this time.")
            } else {
                fileJson.createNewFile()
                fileJson.writeText(Json.encodeToString(userStatsSmall))
                println("\tWrote JSON to ${fileJson.path}")
            }
        }

        if (storeType == StoreType.READABLE || storeType == StoreType.BOTH) {
            if (overrideIfExists.not() && fileText.exists()) {
                println("\tText-File exists. Ignoring this time.")
            } else {
                fileText.createNewFile()
                val stringToWrite = "Name: ${userStatsSmall.name}\nMail: ${userStatsSmall.email}\n\nTagesnachweise:\n${
                    userStatsSmall.dayStats.joinToString(separator = "\n\n") { "${it.date}\nZeit: ${it.workTimeStart} - ${it.workTimeEnd}\nGearbeitet: ${it.workedTime}\nPause: ${it.breakTime}" }
                }"
                fileText.writeText(stringToWrite)
                println("\tWrote JSON to ${fileText.path}")
            }
        }
    }

    fun getLatestUserStatsSmall(): UserStatsSmall? {
        val files = File(timeFilesFolder).listFiles { _, name -> name.endsWith(".json") } ?: return null

        return if (files.size > 1) {
            files.sortByDescending { it.lastModified() } //Absteigend sortieren. Älteste sollte dann das erste Element sein.
            println("\tThere should not be more than 1 File! Return oldest.")
            val fileText = files.first().readText()
            Json.decodeFromString(fileText)
        } else if (files.size == 1) {
            Json.decodeFromString(files.first().readText())
        } else {
            println("\tNo Files to load.")
            null
        }
    }

    fun deleteAllFiles(storeType: StoreType = StoreType.JSON) {
        val files = File(timeFilesFolder).listFiles { _, name ->
            when (storeType) {
                StoreType.JSON -> name.endsWith(".json")
                StoreType.READABLE -> name.endsWith(".txt")
                else -> {
                    name.endsWith(".json") || name.endsWith(".txt")
                }
            }
        }
        files?.forEach { it.delete() }

        println("\tDeletes all files type ${storeType.name} (${files?.size})")
    }

    fun writeNewComparefile(fileInput: String) {
        val file = File(changedEntriesFolder + "changes_" + sdf.format(Date()) + ".txt")
        file.createNewFile()
        file.writeText(fileInput.trim())
        println("\tWrote changes file to ${file.path}")
    }

    enum class StoreType {
        JSON, READABLE, BOTH
    }

}