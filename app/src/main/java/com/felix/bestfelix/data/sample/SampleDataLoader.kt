package com.felix.bestfelix.data.sample

import com.felix.bestfelix.domain.model.CreditDirection
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.domain.model.Priority
import com.felix.bestfelix.domain.model.ProjectStatus
import com.felix.bestfelix.domain.repository.FinanceRepository
import com.felix.bestfelix.domain.repository.HabitRepository
import com.felix.bestfelix.domain.repository.NoteRepository
import com.felix.bestfelix.domain.repository.ProjectRepository
import com.felix.bestfelix.domain.repository.QuitHabitRepository
import com.felix.bestfelix.domain.repository.SavingRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Jeu d'exemples du premier lancement. Tout est préfixé « Exemple · » pour que ce soit
 * reconnaissable et facile à supprimer.
 */
private const val PREFIX = "Exemple · "

class SampleDataLoader @Inject constructor(
    private val habits: HabitRepository,
    private val quitHabits: QuitHabitRepository,
    private val projects: ProjectRepository,
    private val savings: SavingRepository,
    private val finance: FinanceRepository,
    private val notes: NoteRepository,
) {

    suspend fun load(today: LocalDate) {
        loadHabits(today)
        loadQuitHabits(today)
        loadProjects(today)
        loadSavings(today)
        loadFinance(today)
        loadNotes()
    }

    private suspend fun loadHabits(today: LocalDate) {
        // 9 jours d'affilée : la série passe le palier de 7, donc un joker est déjà gagné.
        val reading = habits.createHabit(
            name = "${PREFIX}Lire 20 minutes",
            why = "Pour finir un livre par mois",
            createdAt = today.minusDays(30),
        )
        repeat(9) { offset ->
            habits.setValidated(reading, today.minusDays(offset.toLong()), true)
        }

        val walking = habits.createHabit(
            name = "${PREFIX}Marcher 30 minutes",
            why = null,
            createdAt = today.minusDays(14),
        )
        repeat(3) { offset ->
            habits.setValidated(walking, today.minusDays(offset.toLong() + 1), true)
        }

        habits.createHabit(
            name = "${PREFIX}Coder une heure sur un projet perso",
            why = "Avancer même les jours chargés",
            createdAt = today.minusDays(5),
        )
    }

    private suspend fun loadQuitHabits(today: LocalDate) {
        val cigarette = quitHabits.create("${PREFIX}Cigarette", today.minusDays(20))
        quitHabits.addRelapse(
            quitHabitId = cigarette,
            date = today.minusDays(4),
            note = "Soirée entre amis, j'ai craqué après le repas.",
        )

        quitHabits.create("${PREFIX}Réseaux sociaux le matin", today.minusDays(6))
    }

    private suspend fun loadProjects(today: LocalDate) {
        projects.create(
            title = "${PREFIX}Refonte de l'API",
            context = "JDS",
            currentTask = "Migrer l'authentification",
            priority = Priority.HIGH,
            status = ProjectStatus.ACTIVE,
            dueDate = today.plusDays(5),
        )
        projects.create(
            title = "${PREFIX}Appli de caisse",
            context = "PrimeTech",
            currentTask = "Maquettes de l'écran de vente",
            priority = Priority.MEDIUM,
            status = ProjectStatus.ACTIVE,
            dueDate = null,
        )
        projects.create(
            title = "${PREFIX}Site vitrine",
            context = "Client",
            currentTask = null,
            priority = Priority.LOW,
            status = ProjectStatus.DONE,
            dueDate = today.minusDays(12),
        )
    }

    private suspend fun loadSavings(today: LocalDate) {
        val laptop = savings.create(
            title = "${PREFIX}Ordinateur portable",
            purpose = "Travail",
            targetAmount = 500_000,
            dueDate = today.plusMonths(4),
        )
        savings.addDeposit(laptop, today.minusDays(20), 100_000)
        savings.addDeposit(laptop, today.minusDays(5), 50_000)

        val fund = savings.create(
            title = "${PREFIX}Fonds d'urgence",
            purpose = "Perso",
            targetAmount = 300_000,
            dueDate = null,
        )
        savings.addDeposit(fund, today.minusDays(10), 50_000)
    }

    private suspend fun loadFinance(today: LocalDate) {
        val owed = finance.createCredit(
            direction = CreditDirection.I_OWE,
            counterparty = "${PREFIX}Issa",
            amount = 75_000,
            reason = "Avance sur matériel",
            dueDate = today.plusDays(10),
        )
        finance.addRepayment(owed, today.minusDays(3), 25_000)

        finance.createCredit(
            direction = CreditDirection.OWED_TO_ME,
            counterparty = "${PREFIX}Awa",
            amount = 40_000,
            reason = "Prestation de dépannage",
            dueDate = today.minusDays(3),
        )

        finance.createIncome(150_000, today.withDayOfMonth(5), "${PREFIX}JDS")
        finance.createIncome(60_000, today.minusDays(2), "${PREFIX}Freelance")
        finance.createIncome(120_000, today.minusMonths(1).withDayOfMonth(5), "${PREFIX}JDS")
    }

    private suspend fun loadNotes() {
        notes.create(
            category = NoteCategory.GOOD_HABITS,
            title = "${PREFIX}Ce qui marche",
            body = "Lire juste après le dîner : c'est le moment où je suis le plus tranquille.",
        )
        notes.create(
            category = NoteCategory.PROJECTS,
            title = "${PREFIX}Réunion JDS",
            body = "Valider le schéma d'authentification avant de coder la migration.",
        )
        notes.create(
            category = NoteCategory.FINANCE,
            title = "${PREFIX}Rappel",
            body = "Relancer Awa pour la prestation de dépannage.",
        )
    }
}
