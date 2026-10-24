package com.felix.bestfelix.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "saving_goals")
data class SavingGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    /** Pour quoi : une entreprise, du perso… */
    val purpose: String? = null,
    /** Montant cible en FCFA (entier, sans décimales). */
    val targetAmount: Long,
    val dueDate: LocalDate? = null,
)

@Entity(
    tableName = "deposits",
    foreignKeys = [
        ForeignKey(
            entity = SavingGoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class DepositEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val goalId: Long,
    val date: LocalDate,
    val amount: Long,
)
