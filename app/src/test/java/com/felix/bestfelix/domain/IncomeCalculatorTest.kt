package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.finance.IncomeCalculator
import com.felix.bestfelix.domain.model.Income
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class IncomeCalculatorTest {

    private val incomes = listOf(
        Income(1, 150_000, LocalDate.of(2026, 10, 5), "JDS"),
        Income(2, 50_000, LocalDate.of(2026, 10, 20), "Freelance"),
        Income(3, 200_000, LocalDate.of(2026, 9, 28), "JDS"),
    )

    @Test
    fun `total du mois en cours`() {
        assertEquals(200_000, IncomeCalculator.monthTotal(incomes, TODAY))
    }

    @Test
    fun `groupement par mois, du plus recent au plus ancien`() {
        val months = IncomeCalculator.groupByMonth(incomes)

        assertEquals(listOf(YearMonth.of(2026, 10), YearMonth.of(2026, 9)), months.map { it.month })
        assertEquals(listOf(200_000L, 200_000L), months.map { it.total })
        assertEquals(2, months.first().incomes.size)
    }
}
