import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class UserStatsSmall(val name: String, val userId: Int, val email: String, val dayStats: List<DayStats>)

@Serializable
data class DayStats(
    val date: String,
    val workTimeStart: String,
    val workTimeEnd: String,
    val workedTime: Time,
    val breakTime: Time
) {

    @Transient
    val dateAsLocalDate: LocalDate = LocalDate.parse(date)

    @Transient
    val workTimeStartAsLocalDateTime: LocalDateTime = LocalDateTime.parse(workTimeStart.replace(" ", "T"))

    @Transient
    val workTimeEndAsLocalDateTime: LocalDateTime = LocalDateTime.parse(workTimeEnd.replace(" ", "T"))
}

@Serializable
data class Time(val hours: Int, val minute: Int) {
    val seconds: Int = hours * 3600 + minute * 60
    override fun toString() = "${hours}h ${minute}m"
}