package com.felix.bestfelix.domain.finance

import com.felix.bestfelix.domain.model.Income
import java.time.LocalDate
import java.time.YearMonth

/** Un mois d'entrées, avec son total : c'est l'unité d'affichage de la liste. */
data class IncomeMonth(
    val month: YearMonth,
    val incomes: List<Income>,
) {
    val total: Long = incomes.sumOf { it.amount }
}

object IncomeCalculator {

    /** Entrées du mois en cours, pour la tuile de résumé. */
    fun monthTotal(incomes: List<Income>, today: LocalDate): Long =
        monthTotal(incomes, YearMonth.from(today))

    fun monthTotal(incomes: List<Income>, month: YearMonth): Long =
        incomes.filter { YearMonth.from(it.date) == month }.sumOf { it.amount }

    /** Groupement par mois, du plus récent au plus ancien. */
    fun groupByMonth(incomes: List<Income>): List<IncomeMonth> =
        incomes.groupBy { YearMonth.from(it.date) }
            .toSortedMap(compareByDescending { it })
            .map { (month, list) -> IncomeMonth(month, list.sortedByDescending { it.date }) }
}
