import kotlinx.serialization.Serializable

@Serializable
data class UserStats(
    val userreports: List<Userreport>
)

@Serializable
data class Userreport(
    val diff: Int,
    val holidays_carry: Int,
    val holidays_quota: Int,
    val month_details: List<MonthDetail>,
    val overtime_carryover: Int,
    val overtime_reduced: Int,
    val sum_absence: SumAbsence,
    val sum_hours: Int,
    val sum_reduction_planned: Int,
    val sum_reduction_used: Int,
    val sum_target: Int? = null,
    val users_email: String,
    val users_id: Int,
    val users_name: String,
    val users_number: String? = null,
    val workdays: Int
)

@Serializable
data class MonthDetail(
    val diff: Int,
    val nr: Int,
    val sum_hours: Int,
    val sum_hours_without_compensation: Int,
    val sum_overtime_reduced: Int,
    val sum_reduction_used: Int,
    val sum_target: Int? = null,
    val week_details: List<WeekDetail>
)

@Serializable
data class SumAbsence(
    val home_office: Int,
    val maternity_protection: Int,
    val military_service: Int,
    val out_of_office: Int,
    val quarantine: Int,
    val regular_holidays: Int,
    val school: Int,
    val sick_child: Int,
    val sick_self: Int,
    val special_leaves: Int
)

@Serializable
data class WeekDetail(
    val day_details: List<DayDetail>,
    val diff: Int,
    val nr: Int,
    val sum_hours: Int,
    val sum_reduction_used: Int,
    val sum_target: Int? = null
)

@Serializable
data class DayDetail(
    val breaks: List<Break> = emptyList(),
    val count_absence: CountAbsence,
    val count_reduction_used: Int? = null,
    val date: String,
    val diff: Int? = null, //Nur null, wenn Tag noch in Zukunft
    val hours: Int? = null,
    val hours_without_compensation: Int? = null,
    val nonbusiness: Boolean,
    val target: Int? = null,
    val target_raw: Int? = null,
    val weekday: Int,
    val work_end: String? = null,
    val work_start: String? = null
) {
    fun getBreakTime(): Time {
        var sum = 0
        breaks.forEach {
            sum += it.length
        }
        return Time(sum / 3600, (sum % 3600) / 60)
    }
}

@Serializable
data class Break(
    val length: Int,
    val since: String,
    val until: String
)

@Serializable
data class CountAbsence(
    val home_office: Int,
    val maternity_protection: Int,
    val military_service: Int,
    val out_of_office: Int,
    val quarantine: Int,
    val regular_holidays: Int,
    val school: Int,
    val sick_child: Int,
    val sick_self: Int,
    val special_leaves: Int
)