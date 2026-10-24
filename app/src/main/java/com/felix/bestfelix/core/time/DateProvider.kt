package com.felix.bestfelix.core.time

import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seule source de « aujourd'hui » dans l'appli : le domaine et les écrans la reçoivent par
 * injection, ce qui rend les règles (séries, soldes) testables à date fixe.
 */
interface DateProvider {
    fun today(): LocalDate
}

@Singleton
class SystemDateProvider @Inject constructor() : DateProvider {
    override fun today(): LocalDate = LocalDate.now(ZoneId.systemDefault())
}
