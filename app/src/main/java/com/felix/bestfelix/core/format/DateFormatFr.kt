package com.felix.bestfelix.core.format

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateFormatFr {

    private val FRENCH = Locale.FRENCH
    private val fullFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", FRENCH)
    private val shortFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", FRENCH)
    private val dayMonthFormatter = DateTimeFormatter.ofPattern("d MMM", FRENCH)

    /** « vendredi 23 octobre 2026 » */
    fun full(date: LocalDate): String = date.format(fullFormatter)

    /** « 23 oct. 2026 » */
    fun short(date: LocalDate): String = date.format(shortFormatter)

    /** « 23 oct. » */
    fun dayMonth(date: LocalDate): String = date.format(dayMonthFormatter)

    /** « L », « M »… pour la ligne des 7 pastilles. */
    fun weekdayInitial(date: LocalDate): String =
        date.dayOfWeek.getDisplayName(TextStyle.NARROW, FRENCH).uppercase(FRENCH)

    /** « octobre 2026 », pour les en-têtes de mois des finances. */
    fun monthYear(date: LocalDate): String =
        "${date.month.getDisplayName(TextStyle.FULL, FRENCH)} ${date.year}"
}
