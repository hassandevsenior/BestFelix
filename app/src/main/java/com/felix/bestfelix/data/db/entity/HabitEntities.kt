package com.felix.bestfelix.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.felix.bestfelix.domain.model.DayStatus
import java.time.LocalDate

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** « Pourquoi c'est important », optionnel. */
    val why: String? = null,
    val createdAt: LocalDate,
    /** Ordre d'affichage choisi à la main. */
    val position: Int = 0,
)

/**
 * Un jour validé (ou sauvé par un joker) pour une habitude.
 * C'est la seule source de vérité : XP, séries, niveaux et stock de jokers sont recalculés
 * depuis cette table, jamais stockés.
 */
@Entity(
    tableName = "habit_days",
    primaryKeys = ["habitId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class HabitDayEntity(
    @ColumnInfo(index = true) val habitId: Long,
    val date: LocalDate,
    val status: DayStatus,
)
