package com.felix.bestfelix

import com.felix.bestfelix.core.format.MoneyFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Espace fine insécable et vrai signe moins : les montants doivent rester lisibles. */
private const val NARROW = ' '
private const val MINUS = '−'

class MoneyFormatTest {

    @Test
    fun `montants groupes par milliers avec une espace fine`() {
        assertEquals("125${NARROW}000${NARROW}FCFA", MoneyFormat.format(125_000))
        assertEquals("0${NARROW}FCFA", MoneyFormat.format(0))
        assertEquals("1${NARROW}234${NARROW}567${NARROW}FCFA", MoneyFormat.format(1_234_567))
        assertEquals("900${NARROW}FCFA", MoneyFormat.format(900))
    }

    @Test
    fun `montant signe`() {
        assertEquals("+5${NARROW}000${NARROW}FCFA", MoneyFormat.formatSigned(5_000))
        assertEquals("${MINUS}5${NARROW}000${NARROW}FCFA", MoneyFormat.formatSigned(-5_000))
    }

    @Test
    fun `lecture d'un montant saisi a la main`() {
        assertEquals(125_000L, MoneyFormat.parse("125${NARROW}000 FCFA"))
        assertEquals(125_000L, MoneyFormat.parse("125000"))
        assertNull(MoneyFormat.parse(""))
        assertNull(MoneyFormat.parse("abc"))
    }
}
