import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import kotlin.system.measureTimeMillis

fun main(vararg args: String) {
    FileHandler
    SettingsHandler

    if (args.isNotEmpty() && args.first().toBoolean()) return requestClockodoTimesAndCheck()

    println("Hello ${SettingsHandler.settings.userEmail}.\nPlease enter 'true' as starting parameter to execute the checker!")
}

private fun requestClockodoTimesAndCheck() {
    runBlocking {
        println("Request Clockodo Stats")
        Requests.getUserStats()?.let {
            println("Checking for changes and delete old... ")
            val checking = measureTimeMillis {
                checkForChangesAndDeleteOld(it)
            }
            println("Done in ${checking}ms")
            println("Save new requested Clockodo data...")
            val store = measureTimeMillis {
                FileHandler.createAndWriteNewDay(it, overrideIfExists = true, storeType = FileHandler.StoreType.JSON)
            }
            println("Done in ${store}ms")
        }
    }
}

fun checkForChangesAndDeleteOld(userStatsSmall: UserStatsSmall) {
    val oldStats = FileHandler.getLatestUserStatsSmall() ?: return

    val sb = StringBuilder()
    val localDate = LocalDate.now()

    oldStats.dayStats.forEach { oldDay ->
        if (oldDay.dateAsLocalDate == localDate) return@forEach //Aktuellen Tag ignorieren. Ändert sich zu oft.
        val newDay = userStatsSmall.dayStats.find { it.date == oldDay.date }
        if (newDay == null) {
            println("\tDay not found! -> ${oldDay.date}")
        } else {
            var errorString = ""
            var foundChange = false

            if (oldDay.breakTime.minute == 0 &&
                (newDay.breakTime.minute == 30 || newDay.breakTime.minute == 60) &&
                oldDay.dateAsLocalDate.plusDays(7).isAfter(localDate)
            ) {
                println("\tPause innerhalb 7 Tagen angepasst.In Ordnung und weiter.")
                return@forEach
            } //Pause wurde nachträglich eingetragen. Kann ignoriert werden.

            if (oldDay.workedTime.seconds != newDay.workedTime.seconds) {
                foundChange = true
                "Worked time changed! old: ${oldDay.workedTime} -> new: ${newDay.workedTime}".let {
                    println("\t" + it)
                    errorString += it + "\n"
                }
            }
            if (oldDay.breakTime.seconds != newDay.breakTime.seconds) {
                foundChange = true
                "Break time changed! old: ${oldDay.breakTime} -> new: ${newDay.breakTime}".let {
                    println("\t" + it)
                    errorString += it + "\n"
                }
            }
            if (foundChange) {
                sb.append("\n${oldDay.dateAsLocalDate.let { "${it.dayOfMonth}.${it.monthValue}.${it.year}" }}\n")
                    .append(errorString).append("\n")
            }
        }
    }

    if (sb.isNotBlank()) {
        println("\tFound Changes. Creating compare-file.")
        FileHandler.writeNewComparefile(sb.toString())
    } else {
        println("\tNo changes found. Deleting all Files in times. Gooooood!")
    }

    FileHandler.deleteAllFiles()
}