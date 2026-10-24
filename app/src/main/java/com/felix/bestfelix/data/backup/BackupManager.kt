package com.felix.bestfelix.data.backup

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.felix.bestfelix.data.db.BestFelixDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/** Résumé d'un import ou d'un export, pour l'afficher à l'écran. */
data class BackupSummary(
    val itemCount: Int,
)

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: BestFelixDatabase,
) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /** Écrit tout le contenu de la base dans le fichier choisi via le Storage Access Framework. */
    suspend fun export(uri: Uri): BackupSummary = withContext(Dispatchers.IO) {
        val habitDao = database.habitDao()
        val quitDao = database.quitHabitDao()
        val financeDao = database.financeDao()
        val savingDao = database.savingDao()

        val backup = BackupFile(
            exportedAt = Instant.now().toString(),
            habits = habitDao.getHabits().map { it.toBackup() },
            habitDays = habitDao.getAllDays().map { it.toBackup() },
            quitHabits = quitDao.getQuitHabits().map { it.toBackup() },
            relapses = quitDao.getRelapses().map { it.toBackup() },
            projects = database.projectDao().getProjects().map { it.toBackup() },
            savingGoals = savingDao.getGoals().map { it.toBackup() },
            deposits = savingDao.getDeposits().map { it.toBackup() },
            credits = financeDao.getCredits().map { it.toBackup() },
            repayments = financeDao.getRepayments().map { it.toBackup() },
            incomes = financeDao.getIncomes().map { it.toBackup() },
            notes = database.noteDao().getNotes().map { it.toBackup() },
        )

        val text = json.encodeToString(backup)
        val resolver = context.contentResolver
        // "wt" tronque le fichier : sinon un ancien contenu plus long resterait en queue.
        resolver.openOutputStream(uri, "wt")?.use { stream ->
            stream.write(text.toByteArray())
        } ?: throw BackupFormatException("Impossible d'écrire dans ce fichier.")

        BackupSummary(itemCount = backup.itemCount)
    }

    /** Lit le fichier et remplace **tout** le contenu de la base, dans une seule transaction. */
    suspend fun import(uri: Uri): BackupSummary = withContext(Dispatchers.IO) {
        val text = context.contentResolver.openInputStream(uri)
            ?.use { it.readBytes().decodeToString() }
            ?: throw BackupFormatException("Impossible de lire ce fichier.")

        val backup = runCatching { json.decodeFromString<BackupFile>(text) }
            .getOrElse {
                throw BackupFormatException("Ce fichier n'est pas une sauvegarde Best Félix.")
            }

        if (backup.version > CURRENT_BACKUP_VERSION) {
            throw BackupFormatException(
                "Ce fichier vient d'une version plus récente de l'appli (v${backup.version}).",
            )
        }

        // Les conversions se font avant la transaction : un fichier invalide ne touche pas la base.
        val habits = backup.habits.map { it.toEntity() }
        val habitDays = backup.habitDays.map { it.toEntity() }
        val quitHabits = backup.quitHabits.map { it.toEntity() }
        val relapses = backup.relapses.map { it.toEntity() }
        val projects = backup.projects.map { it.toEntity() }
        val goals = backup.savingGoals.map { it.toEntity() }
        val deposits = backup.deposits.map { it.toEntity() }
        val credits = backup.credits.map { it.toEntity() }
        val repayments = backup.repayments.map { it.toEntity() }
        val incomes = backup.incomes.map { it.toEntity() }
        val notes = backup.notes.map { it.toEntity() }

        database.withTransaction {
            // Les suppressions cascadent sur les jours, rechutes, versements et remboursements.
            database.habitDao().clear()
            database.quitHabitDao().clear()
            database.projectDao().clear()
            database.savingDao().clear()
            database.financeDao().clearCredits()
            database.financeDao().clearIncomes()
            database.noteDao().clear()

            // Parents d'abord : les clés étrangères l'exigent.
            database.habitDao().insertHabits(habits)
            database.habitDao().insertDays(habitDays)
            database.quitHabitDao().insertQuitHabits(quitHabits)
            database.quitHabitDao().insertRelapses(relapses)
            database.projectDao().insertAll(projects)
            database.savingDao().insertGoals(goals)
            database.savingDao().insertDeposits(deposits)
            database.financeDao().insertCredits(credits)
            database.financeDao().insertRepayments(repayments)
            database.financeDao().insertIncomes(incomes)
            database.noteDao().insertAll(notes)
        }

        BackupSummary(itemCount = backup.itemCount)
    }

    /** Nom proposé dans le sélecteur de fichiers. */
    fun suggestedFileName(): String = "best-felix-${java.time.LocalDate.now()}.json"
}
