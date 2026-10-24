package com.felix.bestfelix.core.format

/** Espace fine insécable : « 125 000 FCFA » ne se coupe jamais en fin de ligne. */
private const val THIN_SPACE = '\u202F'

/** Vrai signe moins typographique, à la même largeur que les chiffres. */
private const val MINUS = '\u2212'

/** Le FCFA n'a pas de décimales : tous les montants sont des [Long]. */
object MoneyFormat {

    fun format(amount: Long): String = "${grouped(amount)}${THIN_SPACE}FCFA"

    fun formatSigned(amount: Long): String = when {
        amount > 0 -> "+${format(amount)}"
        else -> format(amount)
    }

    /** Sans suffixe, pour les champs de saisie et les axes de graphique. */
    fun grouped(amount: Long): String {
        val negative = amount < 0
        val digits = kotlin.math.abs(amount).toString()
        val builder = StringBuilder()
        digits.forEachIndexed { index, char ->
            if (index > 0 && (digits.length - index) % 3 == 0) builder.append(THIN_SPACE)
            builder.append(char)
        }
        return if (negative) "$MINUS$builder" else builder.toString()
    }

    /** Lit un montant saisi à la main, espaces et séparateurs compris. */
    fun parse(input: String): Long? =
        input.filter { it.isDigit() }.takeIf { it.isNotEmpty() }?.toLongOrNull()
}
