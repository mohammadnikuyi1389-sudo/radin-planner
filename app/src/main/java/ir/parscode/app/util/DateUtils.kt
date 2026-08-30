package ir.parscode.app.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * The whole app displays dates in the Jalali (Persian) calendar, per the
 * reference screens ("۲۴ آبان ۱۴۰۴"). Storage stays Gregorian ISO
 * ("YYYY-MM-DD") as a stable, unambiguous sort/query key - this class only
 * handles conversion and display formatting, same separation the previous
 * web app used (Gregorian storage key, Jalali only at render time).
 */
object DateUtils {

    private val ISO: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private val JALALI_MONTHS = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند",
    )

    // Week starts Saturday per the reference weekly-view screen.
    private val WEEKDAY_FA = mapOf(
        DayOfWeek.SATURDAY to "شنبه",
        DayOfWeek.SUNDAY to "یکشنبه",
        DayOfWeek.MONDAY to "دوشنبه",
        DayOfWeek.TUESDAY to "سه‌شنبه",
        DayOfWeek.WEDNESDAY to "چهارشنبه",
        DayOfWeek.THURSDAY to "پنجشنبه",
        DayOfWeek.FRIDAY to "جمعه",
    )

    fun todayIso(): String = LocalDate.now().format(ISO)

    fun isoOf(date: LocalDate): String = date.format(ISO)

    fun parseIso(iso: String): LocalDate = LocalDate.parse(iso, ISO)

    fun addDaysIso(iso: String, days: Long): String =
        parseIso(iso).plusDays(days).format(ISO)

    fun weekdayFa(iso: String): String =
        WEEKDAY_FA[parseIso(iso).dayOfWeek] ?: ""

    /** e.g. "۲۴ آبان ۱۴۰۴" */
    fun formatJalaliLong(iso: String): String {
        val g = parseIso(iso)
        val (jy, jm, jd) = gregorianToJalali(g.year, g.monthValue, g.dayOfMonth)
        return "${toPersianDigits(jd)} ${JALALI_MONTHS[jm - 1]} ${toPersianDigits(jy)}"
    }

    /** e.g. "۲۴" - just the Jalali day-of-month, for compact week strips */
    fun jalaliDayOfMonth(iso: String): String {
        val g = parseIso(iso)
        val (_, _, jd) = gregorianToJalali(g.year, g.monthValue, g.dayOfMonth)
        return toPersianDigits(jd)
    }

    private val GREGORIAN_MONTHS_FA = arrayOf(
        "ژانویه", "فوریه", "مارس", "آوریل", "می", "ژوئن",
        "ژوئیه", "اوت", "سپتامبر", "اکتبر", "نوامبر", "دسامبر",
    )

    /** e.g. "۱۵ نوامبر ۲۰۲۵" - Gregorian subtitle under the Jalali date. */
    fun formatGregorianShort(iso: String): String {
        val g = parseIso(iso)
        return "${toPersianDigits(g.dayOfMonth)} ${GREGORIAN_MONTHS_FA[g.monthValue - 1]} ${toPersianDigits(g.year)}"
    }

    fun toPersianDigits(value: Int): String = toPersianDigits(value.toString())

    fun toPersianDigits(value: String): String {
        val fa = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val sb = StringBuilder(value.length)
        for (ch in value) {
            sb.append(if (ch in '0'..'9') fa[ch - '0'] else ch)
        }
        return sb.toString()
    }

    /**
     * Gregorian -> Jalali. Classic algorithm (Pournader/Toossi lineage,
     * used across most open-source Jalali converters); accurate across the
     * app's practical date range without needing an external dependency.
     */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) gy + 1 else gy
        var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) +
            ((gy2 + 399) / 400) + gd + gDaysInMonth[gm - 1]
        var jy = -1595 + (33 * (days / 12053))
        days %= 12053
        jy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val jm: Int
        val jd: Int
        if (days < 186) {
            jm = 1 + (days / 31)
            jd = 1 + (days % 31)
        } else {
            jm = 7 + ((days - 186) / 30)
            jd = 1 + ((days - 186) % 30)
        }
        return Triple(jy, jm, jd)
    }

    /** Jalali -> Gregorian, inverse of [gregorianToJalali]. */
    fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
        val jy2 = jy + 1595
        var days = -355668 + (365 * jy2) + ((jy2 / 33) * 8) + (((jy2 % 33) + 3) / 4) +
            jd + (if (jm < 7) (jm - 1) * 31 else ((jm - 7) * 30) + 186)

        var gy = 400 * (days / 146097)
        days %= 146097
        if (days > 36524) {
            days -= 1
            gy += 100 * (days / 36524)
            days %= 36524
            days += 1
            if (days >= 365) days += 0 // already accounted, kept for clarity
        }
        gy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            gy += (days - 1) / 365
            days = (days - 1) % 365
        }
        var gd = days + 1
        val isLeap = (gy % 4 == 0 && gy % 100 != 0) || gy % 400 == 0
        val gDaysInMonth = intArrayOf(0, 31, if (isLeap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gm = 1
        while (gm <= 12 && gd > gDaysInMonth[gm]) {
            gd -= gDaysInMonth[gm]
            gm++
        }
        return Triple(gy, gm, gd)
    }
}
